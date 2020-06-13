package com.github.commoble.cram;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.lighting.WorldLightManager;

/** Wrapper around a World that makes crammed blocks look like air for position-checking purposes **/
public class AirSimulator implements IWorldReader
{
	private IWorldReader baseWorld;
	
	public AirSimulator(IWorldReader baseWorld)
	{
		this.baseWorld = baseWorld;
	}

	@Override
	public WorldLightManager getLightManager()
	{
		return this.baseWorld.getLightManager();
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		BlockState state = this.baseWorld.getBlockState(pos);
		return state.getBlock() == BlockRegistrar.CRAMMED_BLOCK.get() ? null : this.baseWorld.getTileEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		BlockState state = this.baseWorld.getBlockState(pos);
		return state.getBlock() == BlockRegistrar.CRAMMED_BLOCK.get() ? Blocks.AIR.getDefaultState() : state;
	}

	@Override
	public IFluidState getFluidState(BlockPos pos)
	{
		return this.baseWorld.getFluidState(pos);
	}

	@Override
	public WorldBorder getWorldBorder()
	{
		return this.baseWorld.getWorldBorder();
	}

	@Override
	public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull)
	{
		return this.baseWorld.getChunk(x, z, requiredStatus, nonnull);
	}

	@Override
	@Deprecated
	public boolean chunkExists(int chunkX, int chunkZ)
	{
		return this.baseWorld.chunkExists(chunkX, chunkZ);
	}

	@Override
	public int getHeight(Type heightmapType, int x, int z)
	{
		return this.baseWorld.getHeight(heightmapType, x, z);
	}

	@Override
	public int getSkylightSubtracted()
	{
		return this.baseWorld.getSkylightSubtracted();
	}

	@Override
	public BiomeManager getBiomeManager()
	{
		return this.baseWorld.getBiomeManager();
	}

	@Override
	public Biome getNoiseBiomeRaw(int x, int y, int z)
	{
		return this.baseWorld.getNoiseBiomeRaw(x, y, z);
	}

	@Override
	public boolean isRemote()
	{
		return this.baseWorld.isRemote();
	}

	@Override
	public int getSeaLevel()
	{
		return this.baseWorld.getSeaLevel();
	}

	@Override
	public Dimension getDimension()
	{
		return this.baseWorld.getDimension();
	}

}
