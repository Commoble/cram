package commoble.cram;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

import commoble.cram.api.CramAccessor;
import commoble.cram.api.CramAccessorCapability;
import commoble.cram.util.BlockStateTick;
import commoble.cram.util.NBTListHelper;
import commoble.cram.util.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

public class CrammedTileEntity extends TileEntity
{
	/** CrammedTileEntity holding the properties of a te with an empty substate list **/ 
	protected static final CrammedTileEntity EMPTY_INSTANCE = new CrammedTileEntity();
	
	protected static final NBTListHelper<BlockState> SUBSTATE_SERIALIZER = new NBTListHelper<>("substates",
		NBTUtil::writeBlockState,
		NBTUtil::readBlockState);
	
	protected static final NBTListHelper<BlockStateTick> TICK_SERIALIZER = new NBTListHelper<>("ticks",
		BlockStateTick::toNBT,
		BlockStateTick::fromNBT);
	
	public final CramBlockAccessor accessor;
	public final LazyOptional<CramAccessor> accessorHolder;

	public Set<BlockState> states = new HashSet<>();
	
	public VoxelShape cachedShape = VoxelShapes.empty();
	public VoxelShape cachedCollisionShape = VoxelShapes.empty();
	public VoxelShape cachedRenderShape = VoxelShapes.empty();
	public VoxelShape cachedRaytraceShape = VoxelShapes.empty();
	
	public PriorityQueue<BlockStateTick> pendingTicks = new PriorityQueue<>();
	
	public CrammedTileEntity()
	{
		super(TileEntityRegistrar.CRAMMED_BLOCK.get());
		this.accessor = new CramBlockAccessor(this);
		this.accessorHolder = LazyOptional.of(() -> this.accessor);
	}
	
	@Override
	public void remove()
	{
		// make sure we invalidate the cached cram accessor when the TE is removed
		this.accessorHolder.invalidate();
		super.remove();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if (cap == CramAccessorCapability.INSTANCE)
		{
			return this.accessorHolder.cast();
		}
		return super.getCapability(cap, side);
	}
	
	public static <T> T getSubstateProperty(IBlockReader world, BlockPos pos, Function<CrammedTileEntity, T> getter)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			return getter.apply((CrammedTileEntity) te);
		}
		else
		{
			return getter.apply(EMPTY_INSTANCE);
		}
	}
	
	/**
	 * Returns the sub-blockstates held by a crammed TE at the given position.
	 * Returns an empty collection if no crammed TE exists there.
	 * @param world
	 * @param pos
	 * @return
	 */
	@Nonnull
	public static Collection<BlockState> getBlockStates(IBlockReader world, BlockPos pos)
	{
		return WorldHelper.getTileCapability(world, pos, CramAccessorCapability.INSTANCE)
			.map(CramAccessor::getBlockStates)
			.orElse(EmptyCramAccessor.NO_STATES);
	}

	/**
	 * Add sub-blockstates to the TE-at-the-given-position's list,
	 * this should only be called on the server.
	 * Also recalculates the cached properties and saves and syncs the TE's data.
	 * 
	 * This function assumes that the crammability of the states is already verified
	 * */
	public static void addBlockStates(IWorld world, BlockPos pos, BlockState... states)
	{
		TileEntity maybeTE = world.getTileEntity(pos);
		if (maybeTE instanceof CrammedTileEntity)
		{	// if a crammed block exists here, cram the states into it
			((CrammedTileEntity)maybeTE).addBlockStatesAndUpdate(states);
		}
		else
		{	// otherwise, make a crammed block first, then try to cram the new states into it
			BlockState existingState = world.getBlockState(pos);
			int existingLight = existingState.getLightValue(world, pos);
			int newLight = Arrays.stream(states).map(state -> state.getLightValue(world, pos))
				.reduce(existingLight, Math::max);
			world.setBlockState(pos, BlockRegistrar.CRAMMED_BLOCK.get().getDefaultState().with(CrammedBlock.LIGHT, newLight),0);
			

			maybeTE = world.getTileEntity(pos);
			if (maybeTE instanceof CrammedTileEntity)
			{
				((CrammedTileEntity)maybeTE).addBlockStatesAndUpdate(states);
			}
		}
	}
	
	public void addBlockStatesAndUpdate(BlockState... states)
	{
		for (BlockState state : states)
		{
			this.states.add(state);
		}
		this.updateProperties();
		this.markDirty();
		this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
	}
	
	public void removeBlockStatesAndUpdate(BlockState... states)
	{
		for (BlockState state : states)
		{
			this.states.remove(state);
		}
		
		// if we have 0 or 1 blockstates remaining, return this block to a normal, non-crammed block
		int remainingStates = this.states.size();
		if (remainingStates <= 0)
		{	// if no states remain, set block to air
			this.world.removeBlock(this.pos, false);
		}
		else if (remainingStates == 1)
		{	// if 1 state remains, set block to that state
			// (this seems to be the least bad way to get an object out of a set that only contains it)
			this.states.forEach(state -> this.world.setBlockState(this.pos, state));
		}
		else
		{
			this.updateProperties();
			this.markDirty();
			this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
		}
	}
	
	public void onBlockTick(ServerWorld world, Random random)
	{
		long gameTime = this.world.getGameTime();
		
		for (boolean done = false; !done;)
		{
			// check the next scheduled tick in the queue
			BlockStateTick tick = this.pendingTicks.peek();
			if (tick != null && tick.readyTime <= gameTime)
			{	// if the tick is ready to happen, remove it from the queue and tick it
				this.pendingTicks.poll();
				this.markDirty();
				
				CrammableBlocks.getCramEntryImpl(tick.state.getBlock()).scheduledTickBehavior.onScheduledTick(tick.state, world, this.pos, random, this.accessor);
				
			}
			else
			{
				done = true;
			}
		}
	}
	
	/**
	 * Get the property that the containing CrammedBlock should have at this position,
	 * given the properties of the sub-blockstates stored in this TE.
	 * 
	 * example: the light value of the block should be the highest light value among
	 * all containing blockstates
	 * @param <T>
	 * @param getter
	 * @param baseProperty
	 * @param reducer
	 * @return
	 */
	protected <T> T getCombinedProperty(Function<BlockState, T> getter, T baseProperty, BinaryOperator<T> reducer)
	{
		return this.states.stream()
			.map(getter)
			.reduce(baseProperty, reducer);
	}
	
	private void updateProperties()
	{
		ISelectionContext context = ISelectionContext.dummy(); // TODO make selection context possible?
		VoxelShape emptyVoxel = VoxelShapes.empty();
		this.cachedShape = this.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).shapeGetter.get(state, this.world, this.pos, context), emptyVoxel, VoxelShapes::or);
		this.cachedCollisionShape = this.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).collisionShapeGetter.get(state, this.world, this.pos, context), emptyVoxel, VoxelShapes::or);
		this.cachedRaytraceShape = this.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).raytraceShapeGetter.get(state, this.world, this.pos), emptyVoxel, VoxelShapes::or);
		this.cachedRenderShape = this.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).renderShapeGetter.get(state, this.world, this.pos), emptyVoxel, VoxelShapes::or);

		// don't update light on read
		if (this.world != null)
		{
			BlockState oldState = this.getBlockState();
			if (oldState.hasProperty(CrammedBlock.LIGHT))
			{
				int newLight = this.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).lightGetter.getLight(state, this.world, this.pos), 0, Math::max);
				int oldLight = oldState.get(CrammedBlock.LIGHT);
				if (newLight != oldLight)
				{
					this.world.setBlockState(this.pos, oldState.with(CrammedBlock.LIGHT, newLight));
				}
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT compound)
	{
		super.read(state, compound);
		this.readServerData(compound);
		this.readSyncedData(compound);
		this.updateProperties();
	}
	
	/** read data from NBT that only the server needs **/
	public void readServerData(CompoundNBT compound)
	{
		PriorityQueue<BlockStateTick> queue = new PriorityQueue<>();
		queue.addAll(TICK_SERIALIZER.read(compound));
		this.pendingTicks = queue;
	}
	
	/** read data from NBT that both client and server need **/
	public void readSyncedData(CompoundNBT compound)
	{
		this.states = Sets.newHashSet(SUBSTATE_SERIALIZER.read(compound));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		super.write(compound);
		this.writeServerData(compound);
		this.writeSyncedData(compound);
		return compound;
	}
	
	public CompoundNBT writeServerData(CompoundNBT compound)
	{
		List<BlockStateTick> list = this.pendingTicks.stream()
			.sorted()
			.collect(Collectors.toList());
		TICK_SERIALIZER.write(list, compound);
		return compound;
	}
	
	public CompoundNBT writeSyncedData(CompoundNBT compound)
	{
		SUBSTATE_SERIALIZER.write(this.states, compound);
		return compound;
	}
	
	/** Called on the client when it receives a packet created by getUpdatePacket **/
	@Override
	public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet)
	{
		super.onDataPacket(manager, packet);
		this.readSyncedData(packet.getNbtCompound());
		this.updateProperties();
	}

	/** Called to prepare the NBT for the packet sent from the server when the world is notified of a block update at this position **/
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		CompoundNBT nbt = this.writeSyncedData(new CompoundNBT());
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}

	/** Called to prepare the NBT for the packet sent from the server when the TE is loaded on the client **/
	@Override
	public CompoundNBT getUpdateTag()
	{
		CompoundNBT tag = super.getUpdateTag();
		this.writeSyncedData(tag);
		return tag;
	}
	
	// TODO add sub-TEs, more properties

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return super.getRenderBoundingBox();
	}
}
