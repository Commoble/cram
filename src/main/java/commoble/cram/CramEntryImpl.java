package commoble.cram;

import commoble.cram.api.CramEntry;
import commoble.cram.api.CramReader;
import commoble.cram.api.functions.EntityCollisionBehavior;
import commoble.cram.api.functions.LightGetter;
import commoble.cram.api.functions.NaiveVoxelProvider;
import commoble.cram.api.functions.RandomTickBehavior;
import commoble.cram.api.functions.RedstoneConnectivityPredicate;
import commoble.cram.api.functions.RedstonePowerProvider;
import commoble.cram.api.functions.ScheduledTickBehavior;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext.IVoxelProvider;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class CramEntryImpl implements CramEntry
{
	private final Block block;
	
	// passive properties -- generally safe to defer to the block
	public LightGetter lightGetter = BlockState::getLightValue;
	public IVoxelProvider interactionShapeGetter = AbstractBlockState::getShape;
	public IVoxelProvider collisionShapeGetter = AbstractBlockState::getCollisionShape;
	public IVoxelProvider cameraShapeGetter = AbstractBlockState::getRaytraceShape;
	public NaiveVoxelProvider raytraceShapeGetter = AbstractBlockState::getRayTraceShape;
	public NaiveVoxelProvider renderShapeGetter = AbstractBlockState::getRenderShapeTrue;
	public NaiveVoxelProvider attachmentShapeGetter = AbstractBlockState::getRenderShape; // mcp misname
	public NaiveVoxelProvider insulationShapeGetter = CramEntryImpl::getInsulationShapeDefault;
	public RedstoneConnectivityPredicate redstoneConnectivityGetter = CramEntryImpl::canStateConnectRedstoneDefault;
	public RedstonePowerProvider weakPowerGetter = CramEntryImpl::getWeakPowerDefault;
	public RedstonePowerProvider strongPowerGetter = CramEntryImpl::getStrongPowerDefault;
	
	// active behaviors -- must be explicitly defined
	public EntityCollisionBehavior entityCollisionBehavior = EntityCollisionBehavior.NOPE;
	public ScheduledTickBehavior scheduledTickBehavior = ScheduledTickBehavior.NOPE;
	public RandomTickBehavior randomTickBehavior = RandomTickBehavior.NOPE;
	
	public CramEntryImpl(Block block)
	{
		this.block = block;
	}
	
	public Block getBlock()
	{
		return this.block;
	}

	@Override
	public CramEntry setLightGetter(final LightGetter lightGetter)
	{
		this.lightGetter = lightGetter;
		return this;
	}

	@Override
	public CramEntry setInteractionShapeGetter(final IVoxelProvider shapeGetter)
	{
		this.interactionShapeGetter = shapeGetter;
		return this;
	}

	@Override
	public CramEntry setCollisionShapeGetter(final IVoxelProvider shapeGetter)
	{
		this.collisionShapeGetter = shapeGetter;
		return this;
	}
	
	@Override
	public CramEntry setCameraShapeGetter(final IVoxelProvider shapeGetter)
	{
		this.cameraShapeGetter = shapeGetter;
		return this;
	}

	@Override
	public CramEntry setRenderShapeGetter(final NaiveVoxelProvider shapeGetter)
	{
		this.renderShapeGetter = shapeGetter;
		return this;
	}

	@Override
	public CramEntry setRaytraceShapeGetter(final NaiveVoxelProvider shapeGetter)
	{
		this.raytraceShapeGetter = shapeGetter;
		return this;
	}
	
	@Override
	public CramEntry setAttachmentShapeGetter(final NaiveVoxelProvider shapeGetter)
	{
		this.attachmentShapeGetter = shapeGetter;
		return this;
	}
	
	@Override
	public CramEntry setInsulationShapeGetter(final NaiveVoxelProvider shapeGetter)
	{
		this.insulationShapeGetter = shapeGetter;
		return this;
	}
	
	@Override
	public CramEntry setRedstoneConnectivityGetter(RedstoneConnectivityPredicate getter)
	{
		this.redstoneConnectivityGetter = getter;
		return this;
	}
	
	@Override
	public CramEntry setWeakPowerGetter(RedstonePowerProvider getter)
	{
		this.weakPowerGetter = getter;
		return this;
	}
	
	@Override
	public CramEntry setStrongPowerGetter(RedstonePowerProvider getter)
	{
		this.strongPowerGetter = getter;
		return this;
	}

	@Override
	public CramEntry setEntityCollisionBehavior(final EntityCollisionBehavior behavior)
	{
		this.entityCollisionBehavior = behavior;
		return this;
	}
	
	@Override
	public CramEntry setScheduledTickBehavior(final ScheduledTickBehavior behavior)
	{
		this.scheduledTickBehavior = behavior;
		return this;
	}

	@Override
	public CramEntry setRandomTickBehavior(final RandomTickBehavior behavior)
	{
		this.randomTickBehavior = behavior;
		return this;
	}
	
	// default method helpers
	
	static boolean canStateConnectRedstoneDefault(BlockState state, IBlockReader world, BlockPos pos, Direction side, CramReader cram)
	{
		return state.canConnectRedstone(world, pos, side);
	}
	
	static VoxelShape getInsulationShapeDefault(BlockState state, IBlockReader world, BlockPos pos)
	{
		return state.canProvidePower() ? VoxelShapes.empty() : state.getShape(world, pos);
	}
	
	static int getWeakPowerDefault(BlockState state, IBlockReader world, BlockPos pos, Direction side, CramReader cram)
	{
		return state.getWeakPower(world, pos, side);
	}
	
	static int getStrongPowerDefault(BlockState state, IBlockReader world, BlockPos pos, Direction side, CramReader cram)
	{
		return state.getStrongPower(world, pos, side);
	}
}
