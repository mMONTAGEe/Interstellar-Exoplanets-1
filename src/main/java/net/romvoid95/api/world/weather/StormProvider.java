package net.romvoid95.api.world.weather;

import java.lang.reflect.Field;
import java.util.Random;

import com.google.common.base.Predicate;

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.romvoid95.api.event.ExoStormEvent;
import net.romvoid95.client.gui.rendering.Draw;
import net.romvoid95.client.gui.rendering.OpenGL;
import net.romvoid95.common.CommonUtil;

/**
 * EventBusSubscriber annotation must apply to each individual storm provider.
 * Provider will not work without it.
 **/
@EventBusSubscriber
public abstract class StormProvider implements Predicate<Entity>, IStormProvider {
	protected Random random = new Random();

	protected float[] stormX       = null;
	protected float[] stormZ       = null;
	protected float   stormDensity = 0.0F;
	protected int     rainSoundCounter;
	protected boolean renderStorm;
	protected boolean doGroundParticle;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void clientTickEvent (ClientTickEvent event) {
		if (CommonUtil.getMinecraft().world != null
				&& CommonUtil.getMinecraft().world.provider instanceof IClimateProvider
				&& !CommonUtil.getMinecraft().isGamePaused()) {
			IClimateProvider climate = (IClimateProvider) CommonUtil.getMinecraft().world.provider;

			if (climate.getStormProvider() instanceof StormProvider) {
				StormProvider storm = (StormProvider) climate.getStormProvider();

				if (storm.isStormApplicableTo(CommonUtil.getMinecraft().world.provider)) {
					int s = storm.getStormSize();
					storm.updateStorm(CommonUtil.getMinecraft().world);

					if (storm.stormX == null || storm.stormZ == null) {
						storm.stormX = new float[s * s];
						storm.stormZ = new float[s * s];
						for (int i = 0; i < 32; ++i) {
							float f1 = i - 16;
							for (int j = 0; j < 32; ++j) {
								float f  = j - 16;
								float f2 = MathHelper.sqrt(f * f + f1 * f1);
								storm.stormX[i << 5 | j] = -f1 / f2;
								storm.stormZ[i << 5 | j] = f / f2;
							}
						}
					}

					if (storm.isStormActive(CommonUtil.getMinecraft().world)) {
						storm.renderStorm = true;

						if (storm.stormDensity < 1.0F) {
							//storm.stormDensity += 0.0025F;
						}
					}
					else {
						if (storm.stormDensity >= 0.0F) {
							//storm.stormDensity -= 0.0025F;
						}
						else {
							storm.renderStorm  = false;
							storm.stormDensity = 0.0F;
						}
					}

					if (storm.isStormActive(CommonUtil.getMinecraft().world)) {
						float strength = storm.getStormStrength();

						if (!CommonUtil.getMinecraft().gameSettings.fancyGraphics) {
							strength /= 2.0F;
						}

						if (strength != 0.0F) {
							Entity   entity        = CommonUtil.getMinecraft().getRenderViewEntity();
							World    world         = entity.world;
							BlockPos blockpos      = new BlockPos(entity);
							double   x             = 0.0D;
							double   y             = 0.0D;
							double   z             = 0.0D;
							int      particleCount = 0;
							int      passes        = (int) (100.0F * strength * strength);

							if (CommonUtil.getMinecraft().gameSettings.particleSetting == 1) {
								passes >>= 1;
							}
							else if (CommonUtil.getMinecraft().gameSettings.particleSetting == 2) {
								passes = 0;
							}

							storm.random = new Random();

							for (int i = 0; i < passes; ++i) {
								BlockPos    pos1  = world
										.getPrecipitationHeight(blockpos.add(storm.random.nextInt(10), 0, storm.random.nextInt(10)));
								Biome       biome = world.getBiome(pos1);
								BlockPos    pos2  = pos1.down();
								IBlockState state = world.getBlockState(pos2);

								if (pos1.getY() <= blockpos.getY() + 10 && pos1.getY() >= blockpos.getY() - 10
										&& storm.isStormVisibleInBiome(biome)) {
									double        xOffset = storm.random.nextDouble();
									double        zOffset = storm.random.nextDouble();
									AxisAlignedBB box     = state.getBoundingBox(world, pos2);

									if (state.getMaterial() != Material.LAVA && state.getBlock() != Blocks.MAGMA) {
										if (state.getMaterial() != Material.AIR) {
											++particleCount;

											if (storm.random.nextInt(particleCount) == 0) {
												x = pos2.getX();// + xOffset;
												y = pos2.getY() + 0.1F + box.maxY - 1.0D;
												z = pos2.getZ();// + zOffset;
											}

											double pX = pos2.getX();// + xOffset;
											double pY = pos2.getY() + 0.1F + box.maxY;
											double pZ = pos2.getZ();// + zOffset;
											if (storm.useGroundParticle()) {

												ExoStormEvent stormEvent = new ExoStormEvent.Pre(storm);
												MinecraftForge.EVENT_BUS.post(stormEvent);
												if (stormEvent.isCanceled())
													return;

												storm.spawnParticleOnGround(world, pX, pY, pZ);
												ExoStormEvent stormEventPost = new ExoStormEvent.Post(storm);
												MinecraftForge.EVENT_BUS.post(stormEventPost);
											}
										}
									}
								}
							}

							if (particleCount > 0 && storm.random.nextInt(3) < storm.rainSoundCounter++) {
								storm.rainSoundCounter = 0;

								if (y > blockpos.getY() + 1 && world.getPrecipitationHeight(blockpos)
										.getY() > MathHelper.floor(blockpos.getY())) {
									storm.playStormSound(world, x, y, z);
								}
								else {
									storm.playStormSound(world, x, y, z);
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void worldTickEvent (WorldTickEvent event) {
		if (event.world != null && event.world.provider instanceof IClimateProvider) {
			IClimateProvider climate = (IClimateProvider) event.world.provider;
			climate.getStormProvider().updateStorm(event.world);
		}
	}

	@Override
	public void updateStorm (World world) {
		if (world != null && this.isStormApplicableTo(world.provider) && this.isStormActive(world)) {
			for (Object o : world.loadedEntityList.toArray()) {
				if (o instanceof Entity) {
					Entity entity = (Entity) o;

					if (this.apply(entity)) {
						//entity.motionZ      += 0.03F;
						//entity.motionY      += MathHelper.sin(world.getWorldTime() * 0.4F) * 0.1F;
						entity.fallDistance = 0F;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderLast (RenderWorldLastEvent event) {
		if (CommonUtil.getMinecraft().world != null) {
			if (CommonUtil.getMinecraft().world.provider instanceof IClimateProvider) {
				IClimateProvider climate = (IClimateProvider) CommonUtil.getMinecraft().world.provider;
				StormProvider    storm   = (StormProvider) climate.getStormProvider();

				ExoStormEvent stormEvent = new ExoStormEvent.Pre(storm);
				MinecraftForge.EVENT_BUS.post(stormEvent);
				if (stormEvent.isCanceled())

					return;

				climate.getStormProvider().renderStorm(event.getPartialTicks(), CommonUtil.getMinecraft().world, Minecraft.getMinecraft());
				ExoStormEvent stormEventPost = new ExoStormEvent.Post(storm);
				MinecraftForge.EVENT_BUS.post(stormEventPost);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderStorm (float partialTicks, WorldClient world, Minecraft mc) {
		int rendererUpdateCount = 0;
		try {
			Field fieldRUC = mc.entityRenderer.getClass()
					.getDeclaredField(GCCoreUtil.isDeobfuscated() ? "rendererUpdateCount" : "field_78529_t");
			fieldRUC.setAccessible(true);
			rendererUpdateCount = fieldRUC.getInt(mc.entityRenderer);
		}
		catch (Exception e) {}
		if (!isStormActive(world) && !renderStorm) {
			return;
		}

		if (stormX == null || stormZ == null) {
			return;
		}

		OpenGL.pushMatrix();
		OpenGL.enableLight();
		Entity        entity = CommonUtil.getMinecraft().getRenderViewEntity();
		int           posX   = MathHelper.floor(entity.posX);
		int           posY   = MathHelper.floor(entity.posY);
		int           posZ   = MathHelper.floor(entity.posZ);
		BufferBuilder buffer = Draw.buffer();
		GlStateManager.disableCull();
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.enableBlend();
		OpenGL.blendClear();
		GlStateManager.enableColorMaterial();
		GlStateManager
				.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		if (!doesLightingApply()) {
			OpenGL.disableLight();
		}

		double renderPartialX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double renderPartialY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double renderPartialZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		int    renderYFloor   = MathHelper.floor(renderPartialY);
		int    stormDepth     = 5;
		int    stormHeight    = 6 + stormDepth;

		if (CommonUtil.getMinecraft().gameSettings.fancyGraphics) {
			stormDepth = 10;
		}

		int lastPass = -1;
		buffer.setTranslation(-renderPartialX, -renderPartialY, -renderPartialZ);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int z = posZ - stormDepth; z <= posZ + stormDepth; ++z) {
			int    idx = (z - posZ + 16) * 32;
			for (int x = posX - stormDepth; x <= posX + stormDepth; ++x) {
				double rX  = this.stormX[idx] * 0.5D;
				double rZ  = this.stormZ[idx] * 0.5D;
				pos.setPos(x, 0, z);
				Biome biome = world.getBiome(pos);

				if (isStormVisibleInBiome(biome) && renderStorm) {
					int startHeight = world.getPrecipitationHeight(pos).getY();
					int minY        = posY - stormHeight;
					int maxY        = posY + stormHeight;

					if (minY < startHeight) {
						minY = startHeight;
					}

					if (maxY < startHeight) {
						maxY = startHeight;
					}

					int vY = startHeight;

					if (startHeight < renderYFloor) {
						vY = renderYFloor;
					}

					if (minY != maxY) {
						this.random.setSeed(x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761);
						OpenGL.enableCullFace();

						if (lastPass != 0) {
							if (lastPass >= 0) {
								Tessellator.getInstance().draw();
							}

							lastPass = 0;
							getStormTexture(world, biome).bind();
							buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
						}

						float vTravel = -(((CommonUtil.getMinecraft().world.getWorldTime() + (x * x) + x + (z * z) + z)
								& 31) + partialTicks) / getStormDownfallSpeed();

						int index = idx + x - posX +  16;
						double offsetX  = this.stormX[index] + 0.5F;
						double offsetZ  = this.stormZ[index] + 0.5F;
						float  strength = MathHelper.sqrt(offsetX * offsetX + offsetZ * offsetZ) / stormDepth;
						float  alpha    = ((1.0F - strength * strength) * 0.5F + 0.5F) * getStormDensity();
						pos.setPos(x, vY, z);
						int light     = world.getCombinedLight(pos, 0);
						int lightmapX = light >> 16 & 65535;
						int lightmapY = light & 65535;
						double xx = x + 0.5D;
						double zz = z + 0.5D;
						buffer.pos(xx - offsetX, minY, zz - offsetZ)
								.tex(0.0D, maxY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha)
								.lightmap(lightmapX, lightmapY).endVertex();
						buffer.pos(xx + offsetX, minY, zz + offsetZ)
								.tex(1.0D, maxY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha)
								.lightmap(lightmapX, lightmapY).endVertex();
						buffer.pos(xx + offsetX, maxY, zz + offsetZ)
								.tex(1.0D, minY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha)
								.lightmap(lightmapX, lightmapY).endVertex();
						buffer.pos(xx - offsetX, maxY, zz - offsetZ)
								.tex(0.0D, minY * 0.25D + vTravel).color(1.0F, 1.0F, 1.0F, alpha)
								.lightmap(lightmapX, lightmapY).endVertex();
					}
				}
			}
		}

		if (lastPass >= 0) {
			Tessellator.getInstance().draw();
		}

		buffer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableColorMaterial();
		GlStateManager.alphaFunc(516, 0.1F);
		OpenGL.disableLight();
		OpenGL.popMatrix();
	}

	@Override
	public boolean apply (Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;

			if (player.capabilities.isCreativeMode) {
				return false;
			}
		}

		return true;
	}

	@Override
	public float getStormStrength () {
		return 1F;
	}

	@Override
	public float getStormDensity () {
		return stormDensity;
	}

	@Override
	public boolean isStormVisibleInBiome (Biome biome) {
		return true;
	}

	@Override
	public boolean doesLightingApply () {
		return true;
	}

	@Override
	public boolean useGroundParticle () {
		return false;
	}

	@Override
	public WorldProvider getProvider () {
		return CommonUtil.getMinecraft().world.provider;
	}
}