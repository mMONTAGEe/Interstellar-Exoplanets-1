/**
 * Copyright (C) 2020 Interstellar:  Exoplanets
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.romvoid95.space.trappist1.d;

import java.util.List;

import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace;
import asmodeuscore.core.utils.worldengine.WE_Biome;
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen;
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.romvoid95.api.space.prefab.WorldProviderWE_ExoPlanet;
import net.romvoid95.api.space.utility.AstronomicalConstants;
import net.romvoid95.api.world.ExoDimensions;
import net.romvoid95.api.world.weather.ICloudProvider;
import net.romvoid95.core.ExoFluids;
import net.romvoid95.core.Planets;
import net.romvoid95.space.trappist1.TrappistBlocks;
import net.romvoid95.space.trappist1.d.biomes.BiomeOceananic;
import net.romvoid95.space.trappist1.d.biomes.Trap1D_Island;
import net.romvoid95.space.trappist1.d.client.CloudProviderTrappist1D;

public class WorldProviderTrappist1D extends WorldProviderWE_ExoPlanet {

	public static WE_ChunkProvider chunk;
	private boolean                raining    = false;
	private float                  targetRain = 0.0F;
	private int                    rainTime   = 100;
	private int                    rainChange = 100;

	private CloudProviderTrappist1D clouds          = new CloudProviderTrappist1D();
	private IRenderHandler          climateProvider = clouds;

	@Override
	public boolean canDoRainSnowIce (net.minecraft.world.chunk.Chunk chunk) {
		return true;
	}

	@Override
	public boolean enableAdvancedThermalLevel () {
		return false;
	}

	@Override
	public void genSettings (WE_ChunkProvider cp) {
		chunk = cp;

		cp.createChunkGen_List.clear();
		cp.createChunkGen_InXZ_List.clear();
		cp.createChunkGen_InXYZ_List.clear();
		cp.decorateChunkGen_List.clear();
		((WE_ChunkProviderSpace) cp).worldGenerators.clear();

		WE_TerrainGenerator terrainGenerator = new WE_TerrainGenerator();
		terrainGenerator.worldStoneBlock  = TrappistBlocks.TrappistD.TRAP1D_STONE_2.getDefaultState();
		terrainGenerator.worldSeaGen      = true;
		terrainGenerator.worldSeaGenBlock = ExoFluids.PRESSURED_WATER.getDefaultState();
		terrainGenerator.worldSeaGenMaxY  = 104;
		cp.createChunkGen_List.add(terrainGenerator);

		WE_CaveGen cg = new WE_CaveGen();
		cg.replaceBlocksList.clear();
		cg.addReplacingBlock(terrainGenerator.worldStoneBlock);
		cg.lavaMaxY = 8;
		cp.createChunkGen_List.add(cg);

		cp.biomesList.clear();
		WE_Biome.addBiomeToGeneration(cp, new BiomeOceananic(-4D, 4D));
		WE_Biome.addBiomeToGeneration(cp, new Trap1D_Island(-1.0D, 1.0D));

		WE_Biome.setBiomeMap(cp, 1.2D, 6, 1400.0D, 1.1D);
	}

	@Override
	public CelestialBody getCelestialBody () {
		return Planets.TRAPPIST1D;
	}

	@Override
	public Class<? extends IChunkGenerator> getChunkProviderClass () {
		return WE_ChunkProvider.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getCloudColor (float partialTicks) {
		return new Vec3d(0.3D, 0.3D, 0.3D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight () {
		return 250.0F;
	}

	public ICloudProvider getCloudProvider () {
		return clouds;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer () {
		return climateProvider == null ? climateProvider = new CloudProviderTrappist1D() : climateProvider;

	}

	@Override
	public long getDayLength () {
		return 24000L;
	}

	@Override
	public DimensionType getDimensionType () {
		return ExoDimensions.TRAPPIST_1D;
	}

	@Override
	public ResourceLocation getDungeonChestType () {
		return null;
	}

	@Override
	public int getDungeonSpacing () {
		return 0;
	}

	@Override
	public float getFallDamageModifier () {
		return 0;
	}

	@Override
	public Vector3 getFogColor() {
		float f = 1.0F - this.getStarBrightness(1.0F);
		return new Vector3(250 / 255F * f, 192 / 255F * f, 115 / 255F * f);
	}

	@Override
	public double getFuelUsageMultiplier () {
		return 0;
	}

	@Override
	public float getGravity () {
		return 0.00015f;
	}

	@Override
	public double getHorizon () {
		return 1;
	}

	@Override
	public double getMeteorFrequency() {
		return 0;
	}

	@Override
	public int getMoonPhase (long worldTime) {
		return (int) (((worldTime / this.getDayLength()) % 8L) + 8L) % 8;
	}

	@Override
	public Block getPlanetGrassBlock() {
		return null;
	}

	@Override
	public Vector3 getSkyColor () {
		float f = 1.0F - this.getStarBrightness(1.0F);
		return new Vector3((156f / 255.0F) * f, (156f / 255.0F) * f, (156f / 255.0F) * f);
	}

	@Override
	public float getSolarSize () {
		return 0.3F / this.getCelestialBody().getRelativeDistanceFromCenter().unScaledDistance;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness (float partialTicks) {
		float angle = this.world.getCelestialAngle(partialTicks);
		float value = 1.0F - ((MathHelper.cos(angle * AstronomicalConstants.TWO_PI_F) * 2.0F) + 0.25F);
		value = MathHelper.clamp(value, 0.0F, 1.0F);
		return (value * value * 0.5F) + 0.3F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness (float partialTicks) {
		float f1 = this.world.getCelestialAngle(1.0F);
		float f2 = 1.0F - ((MathHelper.cos(f1 * AstronomicalConstants.TWO_PI_F) * 2.0F) + 0.2F);
		f2 = MathHelper.clamp(f2, 0.0F, 1.0F);
		f2 = 1.2F - f2;
		return f2 * 0.8F;
	}

	@Override
	public List<Block> getSurfaceBlocks () {
		return null;
	}

	@Override
	protected float getThermalValueMod () {
		return 0.0F;
	}

	@Override
	public double getYCoordinateToTeleport () {
		return 800.0D;
	}

	@Override
	public boolean hasSunset () {
		return false;
	}

	@Override
	public boolean isSkyColored () {
		return true;
	}

	@Override
	public void onChunkProvider (int cX, int cZ, ChunkPrimer primer) {

	}

	@Override
	public void onPopulate (int x, int z) {}

	@Override
	public void recreateStructures (Chunk chunkIn, int x, int z) {}

	@Override
	public boolean shouldDisablePrecipitation () {
		return false;
	}

	@Override
	public boolean shouldForceRespawn () {
		return !ConfigManagerCore.forceOverworldRespawn;
	}

	@Override
	protected void updateWeatherOverride () {
		if (!this.world.isRemote) {
			if (--this.rainTime <= 0) {
				this.raining = !this.raining;
				if (this.raining) {
					this.rainTime = (this.world.rand.nextInt(3600) + 1000);
				}
				else {
					this.rainTime = (this.world.rand.nextInt(2000) + 1000);
				}
			}

			if (--this.rainChange <= 0) {
				this.targetRain = 0.15F + (this.world.rand.nextFloat() * 0.45F);
				this.rainChange = (this.world.rand.nextInt(200) + 100);
			}

			float strength = this.world.rainingStrength;
			this.world.prevRainingStrength = strength;
			if (this.raining && (strength < this.targetRain)) {
				strength += 0.004F;
			}
			else if (!this.raining || (strength > this.targetRain)) {
				strength -= 0.004F;
			}
			this.world.rainingStrength = MathHelper.clamp(strength, 0.0F, 0.6F);
		}
	}
}
