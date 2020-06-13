package com.github.commoble.cram;

import java.util.Collection;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
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

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedShape);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedCollisionShape);
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedRenderShape);
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return CrammedTileEntity.getSubstateProperty(world, pos, te -> te.cachedRaytraceShape);
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
			Vec3d hit = target.getHitVec();
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
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
	{
		super.onEntityCollision(state, worldIn, pos, entityIn);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
	{
		super.randomTick(state, worldIn, pos, random);
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
	{
		super.tick(state, worldIn, pos, rand);
	}
	
	// TODO implement the rest of these

	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
	{
		return super.isLadder(state, world, pos, entity);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
	{
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public boolean isAir(BlockState state, IBlockReader world, BlockPos pos)
	{
		return super.isAir(state, world, pos);
	}

	@Override
	public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, Entity exploder, Explosion explosion)
	{
		return super.getExplosionResistance(state, world, pos, exploder, explosion);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return super.canConnectRedstone(state, world, pos, side);
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
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(state, world, pos, neighbor);
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
	{
		return super.shouldCheckWeakPower(state, world, pos, side);
	}

	@Override
	public boolean getWeakChanges(BlockState state, IWorldReader world, BlockPos pos)
	{
		return super.getWeakChanges(state, world, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
	{
		return super.getSoundType(state, world, pos, entity);
	}

	@Override
	public BlockState getStateForPlacement(BlockState state, Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand)
	{
		return super.getStateForPlacement(state, facing, state2, world, pos1, pos2, hand);
	}

	@Override
	public boolean canBeConnectedTo(BlockState state, IBlockReader world, BlockPos pos, Direction facing)
	{
		return super.canBeConnectedTo(state, world, pos, facing);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return super.getMaterialColor(state, worldIn, pos);
	}

	@Override
	public void updateNeighbors(BlockState stateIn, IWorld worldIn, BlockPos pos, int flags)
	{
		super.updateNeighbors(stateIn, worldIn, pos, flags);
	}

	@Override
	public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags)
	{
		super.updateDiagonalNeighbors(state, worldIn, pos, flags);
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
	public float getBlockHardness(BlockState blockState, IBlockReader worldIn, BlockPos pos)
	{
		return super.getBlockHardness(blockState, worldIn, pos);
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
	public void spawnAdditionalDrops(BlockState state, World worldIn, BlockPos pos, ItemStack stack)
	{
		super.spawnAdditionalDrops(state, worldIn, pos, stack);
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
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		return super.getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean canProvidePower(BlockState state)
	{
		return super.canProvidePower(state);
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		return super.getStrongPower(blockState, blockAccess, pos, side);
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
	public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile)
	{
		super.onProjectileCollision(worldIn, state, hit, projectile);
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
	public Vec3d getOffset(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return super.getOffset(state, worldIn, pos);
	}

	@Override
	public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
	{
		return super.getSlipperiness(state, world, pos, entity);
	}

	
}
