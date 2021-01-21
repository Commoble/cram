package commoble.cram;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
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
	@Deprecated
	public int getSeaLevel()
	{
		return this.baseWorld.getSeaLevel();
	}

	// get shading?
	@Override
	public float func_230487_a_(Direction dir, boolean flag)
	{
		return this.baseWorld.func_230487_a_(dir, flag);
	}

	@Override
	public FluidState getFluidState(BlockPos pos)
	{
		return this.baseWorld.getFluidState(pos);
	}

	// get collisions for entity
	@Override
	public Stream<VoxelShape> func_230318_c_(Entity entity, AxisAlignedBB aabb, Predicate<Entity> predicate)
	{
		return this.baseWorld.func_230318_c_(entity, aabb, predicate);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return this.baseWorld.getDimensionType();
	}

}
