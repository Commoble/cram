package commoble.cram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import commoble.cram.api.CramAccessor;
import commoble.cram.api.CramReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.CramVoxelShapeHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrammedBlock extends Block
{
	// we need to use a property for light level because light is calculated before the TE is set
	public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 15);
	
	public CrammedBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(LIGHT, 0));
	}
	
	// cramblock-specific methods
	
	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(LIGHT);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileEntityRegistrar.CRAMMED_BLOCK.get().create();
	}
	
	@Override
	@Deprecated
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
		if (te instanceof CrammedTileEntity)
		{
			List<ItemStack> results = new ArrayList<>();
			CrammedTileEntity cramTE = (CrammedTileEntity) te;
			cramTE.states.forEach(subState -> results.addAll(subState.getDrops(builder)));
			return results;
		}

		return super.getDrops(state, builder);
	}
	
    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     *
     * Return true if the block is actually destroyed.
     *
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param state The current state.
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param pos Block position in world
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *        Can be useful to delay the destruction of tile entities till after harvestBlock
     * @param fluid The current fluid state at current position
     * @return True if the block is actually destroyed.
     */
    @Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
    	// raytrace against the substates, get the state the player is actually pointing at
    	// we can reasonably assume that the existing raytracing is currently and successfully targeting at least one
    	// of the interaction shapes belonging to a substate in the crammed block
    	// (otherwise this method wouldn't have been called)
    	TileEntity te = world.getTileEntity(pos);
		@Nullable BlockState targetState = null;
		
		if (te instanceof CrammedTileEntity && player != null)
		{
			CrammedTileEntity cramTE = (CrammedTileEntity) te;
			Vector3d startVec = player.getEyePosition(1F);
			Vector3d lookOffset = player.getLook(1F);
			double rayTraceDistance = 10F;
			Vector3d endVec = startVec.add(lookOffset.x * rayTraceDistance, lookOffset.y * rayTraceDistance, lookOffset.z * rayTraceDistance);
			BlockRayTraceResult rayTraceAgainstAll = cramTE.cachedInteractionShape.rayTrace(startVec, endVec, pos); 
			if (rayTraceAgainstAll != null)
			{
				ISelectionContext selectionContext = ISelectionContext.forEntity(player);
				// adjust hit vector slightly so it's just inside the shape
				Vector3d hitVec = rayTraceAgainstAll.getHitVec().add(lookOffset.mul(0.001D, 0.001D, 0.001D));
				for (BlockState subState : cramTE.states)
				{
					VoxelShape interactionShape = CrammableBlocks.getCramEntryImpl(subState.getBlock()).interactionShapeGetter.get(subState, world, pos, selectionContext);
					if (CramVoxelShapeHelper.shapeContains(interactionShape, hitVec.getX() - pos.getX(), hitVec.getY() - pos.getY(), hitVec.getZ() - pos.getZ()))
					{
						targetState = subState;
						break;
					}
				}
				// remove target state outside of the iterator
				if (targetState != null)
				{
					Block targetBlock = targetState.getBlock();
					// TODO if we add support for TEs then we need to provide TE context here for spawning drops
					// normally xp is dropped too, but we can ignore this since crammed blocks will have been placed by
					// the player anyway
					targetBlock.onPlayerDestroy(world, pos, targetState);
					if (willHarvest)
					{
						targetBlock.harvestBlock(world, player, pos, targetState, null, player.getHeldItemMainhand().copy());
					}
					if (world.isRemote)
					{
						// on the server world, onBlockHarvested will play the break effects for each player except the player given
						// and also anger piglins at that player, so we do want to give it the player
						// on the client world, willHarvest is always false, so we need to manually play the effects for that player
						world.playEvent(player, 2001, pos, Block.getStateId(targetState));
					}
					else
					{
						targetBlock.onBlockHarvested(world, pos, targetState, player);
					}
					cramTE.removeBlockStatesAndUpdate(targetState);
//					world.playEvent(2001, pos, Block.getStateId(targetState));
					return false;
				}
			}
		}
    	
    	// if we failed to remove the substate, just use the regular behaviour
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
	{
		return state.get(LIGHT);
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() == newState.getBlock())
		{
			// only thing super.onReplaced does is remove the tile entity
			// if the block stays the same, we specifically do NOT remove the tile entity
			// so don't do anything here
		}
		else
		{
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	// interaction shape
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedInteractionShape);
	}

	// collision shape
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedCollisionShape);
	}

	// camera shape
	@Override
	public VoxelShape getRayTraceShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedCameraShape);
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedRenderShape);
	}

	// normal override shape
	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedRaytraceShape);
	}

	// attachment shape
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedAttachmentShape);
	}
	
	

	/**
	 * Called periodically clientside on blocks near the player to show effects
	 * (like furnace fire particles). Note that this method is unrelated to
	 * {@link randomTick} and {@link #needsRandomTick}, and will always be called
	 * regardless of whether the block can receive random update ticks
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		Collection<BlockState> states = CrammedTileEntity.getBlockStates(worldIn, pos);
		int size = states.size();
		if (size > 0)
		{
			// this isn't a great way to get a random thing from a collection
			// but sets aren't great at getting-random-things-from-them in general
			// (and we need the state collection to be a set more than we need it to be a list)
			
			BlockState subState = ImmutableList.copyOf(states).get(rand.nextInt(size));
			subState.getBlock().animateTick(subState, worldIn, pos, rand);
		}
	}

	/**
	 * Spawn particles for when the block is destroyed. Due to the nature of how
	 * this is invoked, the x/y/z locations are not always guaranteed to host your
	 * block. So be sure to do proper sanity checks before assuming that the
	 * location is this block.
	 *
	 * @param world
	 *            The current world
	 * @param pos
	 *            Position to spawn the particle
	 * @param manager
	 *            A reference to the current particle manager.
	 * @return True to prevent vanilla break particles from spawning.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean addDestroyEffects(BlockState cramState, World world, BlockPos pos, ParticleManager manager)
	{
		// forge says to double-check the position so let's do that
		if (world.getBlockState(pos).getBlock() == this)
		{
			CrammedTileEntity.getBlockStates(world, pos).forEach(subState -> manager.addBlockDestroyEffects(pos, subState));
		}
		// return true in any case because we don't want to spawn particles even if we don't do the above
		return true;
	}

	/**
	 * Spawn a digging particle effect in the world, this is a wrapper around
	 * EffectRenderer.addBlockHitEffects to allow the block more control over the
	 * particles. Useful when you have entirely different texture sheets for
	 * different sides/locations in the world.
	 *
	 * @param state
	 *            The current state
	 * @param world
	 *            The current world
	 * @param target
	 *            The target the player is looking at {x/y/z/side/sub}
	 * @param manager
	 *            A reference to the current particle manager.
	 * @return True to prevent vanilla digging particles form spawning.
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager)
	{
		if (target instanceof BlockRayTraceResult)
		{
			Vector3d hit = target.getHitVec();
			BlockPos pos = new BlockPos(hit);
			Collection<BlockState> states = CrammedTileEntity.getBlockStates(world, pos);
			for (BlockState subState : states)
			{
				if (subState.getShape(world, pos).getBoundingBox().contains(hit))
				{
					manager.addBlockHitEffects(pos, (BlockRayTraceResult)target);
				}
			}
			
			return true; // tell the game we did our own hit effects and don't need the regular ones
		}
		else
		{
			return false;
		}
	}

	@Override
	public void onEntityCollision(BlockState stateIn, World worldIn, BlockPos pos, Entity entityIn)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			CramAccessor cram = ((CrammedTileEntity)te).accessor;
			// copy the blockstate list, as the original list may be edited during iteration
			List<BlockState> stateListCopy = Lists.newArrayList(cram.getBlockStates());
			for (BlockState state : stateListCopy)
			{
				CrammableBlocks.getCramEntryImpl(state.getBlock()).entityCollisionBehavior.onEntityCollision(state, worldIn, pos, entityIn, cram);
			}
		}
	}
	
	@Override
	public void randomTick(BlockState stateIn, ServerWorld worldIn, BlockPos pos, Random random)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			CramAccessor cram = ((CrammedTileEntity)te).accessor;
			// copy the blockstate list, as the original list may be edited during iteration
			List<BlockState> stateListCopy = Lists.newArrayList(cram.getBlockStates());
			for (BlockState state : stateListCopy)
			{
				if (state.ticksRandomly())
				{
					CrammableBlocks.getCramEntryImpl(state.getBlock()).randomTickBehavior.onRandomTick(state, worldIn, pos, random, cram);				
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			((CrammedTileEntity)te).onBlockTick(worldIn, rand);
		}
	}

	@Override
	public boolean canProvidePower(BlockState state)
	{
		// we don't have world context so we have to return true here
		// we can use IForgeBlock::canConnectRedstone to fine-tune this
		// but we still need to override this since WorldEntitySpawner and RedstoneDiode check this directly
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState stateIn, IBlockReader worldIn, BlockPos pos, Direction side)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			CramAccessor cram = ((CrammedTileEntity)te).accessor;
			for (BlockState state : cram.getBlockStates())
			{
				if (CrammableBlocks.getCramEntryImpl(state.getBlock()).redstoneConnectivityGetter.test(state, worldIn, pos, side, cram))
				{
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader worldIn, BlockPos pos, Direction side)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			CrammedTileEntity cramTE = (CrammedTileEntity)te;
			CramReader cram = cramTE.accessor;
			return cramTE.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).weakPowerGetter.getValue(state, worldIn, pos, side, cram), 0, Math::max);
		}
		
		return 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader worldIn, BlockPos pos, Direction side)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof CrammedTileEntity)
		{
			CrammedTileEntity cramTE = (CrammedTileEntity)te;
			CramReader cram = cramTE.accessor;
			return cramTE.getCombinedProperty(state -> CrammableBlocks.getCramEntryImpl(state.getBlock()).strongPowerGetter.getValue(state, worldIn, pos, side, cram), 0, Math::max);
		}
		
		return 0;
	}
	
	// TODO these are important to implement

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(state, world, pos, neighbor);
	}

	@Override
	public boolean getWeakChanges(BlockState state, IWorldReader world, BlockPos pos)
	{
		return super.getWeakChanges(state, world, pos);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return super.hasComparatorInputOverride(state);
	}
	
	// TODO implement the rest of these

	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
	{
		return super.isLadder(state, world, pos, entity);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public void onPlantGrow(BlockState state, IWorld world, BlockPos pos, BlockPos source)
	{
		super.onPlantGrow(state, world, pos, source);
	}

	@Override
	public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
	{
		return super.getSoundType(state, world, pos, entity);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
	{
		return super.allowsMovement(state, worldIn, pos, type);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return super.propagatesSkylightDown(state, reader, pos);
	}
	
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state)
	{
		super.onPlayerDestroy(worldIn, pos, state);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos)
	{
		return super.getContainer(state, worldIn, pos);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
	{
		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}

	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn)
	{
		super.onExplosionDestroy(worldIn, pos, explosionIn);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
	{
		super.onEntityWalk(worldIn, pos, entityIn);
	}

	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
	{
		super.onBlockClicked(state, worldIn, pos, player);
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack)
	{
		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param)
	{
		return super.eventReceived(state, worldIn, pos, id, param);
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
	{
		super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
	}

	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn)
	{
		super.onLanded(worldIn, entityIn);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void fillWithRain(World worldIn, BlockPos pos)
	{
		super.fillWithRain(worldIn, pos);
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
	{
		return super.getComparatorInputOverride(blockState, worldIn, pos);
	}

	@Override
	public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
	{
		return super.getSlipperiness(state, world, pos, entity);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return super.getStateForPlacement(context);
	}

	@Override
	public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags, int recursionLeft)
	{
		super.updateDiagonalNeighbors(state, worldIn, pos, flags, recursionLeft);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return super.isSideInvisible(state, adjacentBlockState, side);
	}

	@Override
	public boolean isTransparent(BlockState state)
	{
		return super.isTransparent(state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return super.rotate(state, rot);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn)
	{
		return super.mirror(state, mirrorIn);
	}

	@Override
	public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack)
	{
		super.spawnAdditionalDrops(state, worldIn, pos, stack);
	}

	@Override
	public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
	{
		super.onProjectileCollision(worldIn, state, hit, projectile);
	}

	
}
