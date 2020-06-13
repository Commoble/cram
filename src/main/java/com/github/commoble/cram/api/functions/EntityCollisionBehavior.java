package com.github.commoble.cram.api.functions;

import javax.annotation.Nullable;

import com.github.commoble.cram.api.CramAccess;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface EntityCollisionBehavior
{
	/** Default entity collision behavior **/
	public static final EntityCollisionBehavior NOPE = (state, world, pos, entity, access) -> state;
	
	/**
	 * 
	 * @param state The state that an entity collided with
	 * @param world
	 * @param pos The position of the crammed block
	 * @param entity The entity that the state collided with
	 * @param access Access to the crammed block to adjust its states
	 * @return The blockstate to replace the existing state with, if any. Return null to remove
	 * the state from the crammed block. If the returned state is not allowed to be crammable
	 * by block tags, or there is no room for the new state, it will be removed (dropping the block's
	 * item if possible). Return the original state if it should not be replaced.
	 */
	@Nullable
	public BlockState onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CramAccess access);
}
