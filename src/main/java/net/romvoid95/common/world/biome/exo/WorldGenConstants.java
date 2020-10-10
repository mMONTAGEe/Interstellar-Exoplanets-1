package net.romvoid95.common.world.biome.exo;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.*;

import net.romvoid95.common.world.biome.features.WorldGenFallenTree;

public interface WorldGenConstants {
	IBlockState	OAK_LOG			= Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT,
			BlockPlanks.EnumType.OAK);
	IBlockState	BIRCH_LOG		= Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT,
			BlockPlanks.EnumType.BIRCH);
	IBlockState	SPRUCE_LOG		= Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT,
			BlockPlanks.EnumType.SPRUCE);
	IBlockState	JUNGLE_LOG		= Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT,
			BlockPlanks.EnumType.JUNGLE);
	IBlockState	ACACIA_LOG		= Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT,
			BlockPlanks.EnumType.ACACIA);
	IBlockState	DARK_OAK_LOG	= Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT,
			BlockPlanks.EnumType.DARK_OAK);
	IBlockState	FIR_LOG			= Blocks.GRASS.getDefaultState();

	IBlockState	OAK_LEAVES				= Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT,
			BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, false);
	IBlockState	BIRCH_LEAVES			= Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT,
			BlockPlanks.EnumType.BIRCH).withProperty(BlockLeaves.CHECK_DECAY, false);
	IBlockState	SPRUCE_LEAVES			= Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT,
			BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, false);
	IBlockState	JUNGLE_LEAVES			= Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT,
			BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, false);
	IBlockState	ACACIA_LEAVES			= Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT,
			BlockPlanks.EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, false);
	IBlockState	DARK_OAK_LEAVES			= Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT,
			BlockPlanks.EnumType.DARK_OAK).withProperty(BlockLeaves.CHECK_DECAY, false);
	IBlockState	RED_AUTUMNAL_LEAVES		= Blocks.GRASS.getDefaultState();
	IBlockState	BROWN_AUTUMNAL_LEAVES	= Blocks.GRASS.getDefaultState();
	IBlockState	ORANGE_AUTUMNAL_LEAVES	= Blocks.GRASS.getDefaultState();
	IBlockState	YELLOW_AUTUMNAL_LEAVES	= Blocks.GRASS.getDefaultState();
	IBlockState	FIR_LEAVES				= Blocks.GRASS.getDefaultState();

	IBlockState	SAND	= Blocks.SAND.getDefaultState();
	IBlockState	GRASS	= Blocks.GRASS.getDefaultState();

	WorldGenFallenTree	OAK_FALLEN_TREE_FEATURE	= new WorldGenFallenTree(true);
	WorldGenShrub		OAK_SHRUB_FEATURE		= new WorldGenShrub(OAK_LOG, OAK_LEAVES);
	WorldGenTrees		TALLER_OAK_TREE_FEATURE	= new WorldGenTrees(false, 7, OAK_LOG, OAK_LEAVES, false);
	WorldGenSavannaTree	ACACIA_TREE_FEATURE		= new WorldGenSavannaTree(false);
}