package net.rom.exoplanets.content.item;

import micdoodle8.mods.galacticraft.api.entity.IRocketType;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GCFluids;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.rom.exoplanets.content.entities.EntityTwoPlayerRocket;

public class ItemTwoPlayerRocket extends ItemBase implements IHoldableItem {
	
    public ItemTwoPlayerRocket() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        boolean padFound = false;
        TileEntity tile = null;
        if (worldIn.isRemote && playerIn instanceof EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(8, (EntityPlayerSP)playerIn);
            return EnumActionResult.PASS;
        } else {
            float centerX = -1.0F;
            float centerY = -1.0F;
            float centerZ = -1.0F;

            for(int i = -1; i < 2; ++i) {
                for(int j = -1; j < 2; ++j) {
                    BlockPos pos1 = pos.add(i, 0, j);
                    IBlockState state = worldIn.getBlockState(pos1);
                    Block id = state.getBlock();
                    int meta = id.getMetaFromState(state);
                    if (id == GCBlocks.landingPadFull && meta == 0) {
                        padFound = true;
                        tile = worldIn.getTileEntity(pos.add(i, 0, j));
                        centerX = (float)(pos.getX() + i) + 0.5F;
                        centerY = (float)pos.getY() + 0.4F;
                        centerZ = (float)(pos.getZ() + j) + 0.5F;
                        break;
                    }
                }

                if (padFound) {
                    break;
                }
            }

            if (padFound) {
                if (!placeRocketOnPad(stack, worldIn, tile, centerX, centerY, centerZ)) {
                    return EnumActionResult.FAIL;
                } else {
                    if (!playerIn.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }

                    return EnumActionResult.SUCCESS;
                }
            } else {
                return EnumActionResult.PASS;
            }
        }
    }

    public static boolean placeRocketOnPad(ItemStack stack, World worldIn, TileEntity tile, float centerX, float centerY, float centerZ) {
        if (tile instanceof TileEntityLandingPad) {
            if (((TileEntityLandingPad)tile).getDockedEntity() != null) {
                return false;
            } else {
                EntityTwoPlayerRocket spaceship = new EntityTwoPlayerRocket(worldIn, (double)centerX, (double)centerY, (double)centerZ, IRocketType.EnumRocketType.values()[stack.getItemDamage()]);
                spaceship.setPosition(spaceship.posX, spaceship.posY + spaceship.getOnPadYOffset(), spaceship.posZ);
                worldIn.spawnEntity(spaceship);
                if (spaceship.rocketType.getPreFueled()) {
                    spaceship.fuelTank.fill(new FluidStack(GCFluids.fluidFuel, spaceship.getMaxFuel()), true);
                } else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("RocketFuel")) {
                    spaceship.fuelTank.fill(new FluidStack(GCFluids.fluidFuel, stack.getTagCompound().getInteger("RocketFuel")), true);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH) {
            for(int i = 0; i < IRocketType.EnumRocketType.values().length; ++i) {
                list.add(new ItemStack(this, 1, i));
            }
        }

    }

    public boolean shouldHoldLeftHandUp(EntityPlayer player) {
        return true;
    }

    public boolean shouldHoldRightHandUp(EntityPlayer player) {
        return true;
    }

    public boolean shouldCrouch(EntityPlayer player) {
        return true;
    }
}