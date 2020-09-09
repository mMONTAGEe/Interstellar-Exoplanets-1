package net.romvoid95.common.world.biome.exo;

import java.util.Random;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.romvoid95.common.world.biome.BiomeSpace;

public class BiomeMiniJungle extends BiomeSpace implements WorldGenConstants {

	public static BiomeProperties properties = new BiomeProperties("Mini Jungle");

	public BiomeMiniJungle() {
		super(properties);
		decorator.treesPerChunk     = 30;
		decorator.flowersPerChunk   = 5;
		decorator.grassPerChunk     = 25;
		decorator.reedsPerChunk     = 2;
		decorator.clayPerChunk      = 3;
		decorator.waterlilyPerChunk = 12;

		properties.setTemperature(Biomes.JUNGLE.getDefaultTemperature());
		properties.setRainfall(Biomes.JUNGLE.getRainfall());
		properties.setBaseHeight(-0.1F);
		properties.setHeightVariation(0.5F);
		properties.setWaterColor(0xFF24B01C);

	}

	@Override
	public int getSkyColorByTemp (float currentTemperature) {
		//        if (TraverseConfig.disableCustomSkies)
		//            return super.getSkyColorByTemp(currentTemperature);
		//        else
		return 0xFFC2FFEB;
	}

	public WorldGenerator getRandomWorldGenForGrass (Random rand) {
		return rand.nextInt(4) == 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN)
				: new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
	}

	@Override
	public WorldGenAbstractTree getRandomTreeFeature (Random rand) {
		return rand.nextInt(10) == 0 ? BIG_TREE_FEATURE
				: new WorldGenTrees(false, 2 + rand.nextInt(3), JUNGLE_LOG, JUNGLE_LEAVES, true);
	}

	public void decorate (World worldIn, Random rand, BlockPos pos) {
		super.decorate(worldIn, rand, pos);
		int i      = rand.nextInt(16) + 8;
		int j      = rand.nextInt(16) + 8;
		int height = worldIn.getHeight(pos.add(i, 0, j)).getY() * 2; // could == 0, which crashes nextInt
		if (height < 1)
			height = 1;
		int k = rand.nextInt(height);
		if (net.minecraftforge.event.terraingen.TerrainGen
				.decorate(worldIn, rand, pos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.PUMPKIN))
			(new WorldGenMelon()).generate(worldIn, rand, pos.add(i, k, j));
		WorldGenVines worldgenvines = new WorldGenVines();

		if (net.minecraftforge.event.terraingen.TerrainGen
				.decorate(worldIn, rand, pos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS))
			for (j = 0; j < 50; ++j) {
				k = rand.nextInt(16) + 8;
				int l  = 128;
				int i1 = rand.nextInt(16) + 8;
				worldgenvines.generate(worldIn, rand, pos.add(k, 128, i1));
				EntityPig pig = new EntityPig(worldIn);
			}
	}
}
