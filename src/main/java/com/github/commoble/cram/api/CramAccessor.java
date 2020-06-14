package com.github.commoble.cram.api;

import java.util.Collection;

import net.minecraft.block.BlockState;

/**
 * This interface permits limited access to a crammed block.
 * you can get a reference to the capability provider via

 */
public interface CramAccessor
{
	
	/**
	 * @return The collection of blockstates that are crammed
	 */
	public Collection<BlockState> getBlockStates();

	/**
	 * Returns whether there is room for a given blockstate to be added without overlapping.
	 * This method is potentially expensive, try not to call in tick or render methods.
	 * 
	 * @param state The state to check for whether sufficient room exists to add
	 * @param ignoreStates Optional states that can be ignored when checking whether room for the other state exists
	 * @return True if the new state is permitted to be added by tags, does not already exist in the block, and
	 * does not overlap with any states in the crammed block not specified for ignoring by ignoreStates
	 */
	public boolean canStateBeAdded(BlockState state, BlockState... ignoreStates);
	
	/**
	 * Attempt to add a blockstate to a crammed block.
	 * If there is no room for the state, or the new state is not permitted to be crammed by block tags,
	 * or the block already contains the state, then the state will not be added.
	 * @param state
	 * @param simulate If true, will not actually add the state, without affecting the return value.
	 * @return true if the state was or would have been successfully added
	 */
	public boolean addState(BlockState state, boolean simulate);
	
	/**
	 * Attempts to replace an existing blockstate with a new blockstate.
	 * If the new blockstate is not allowed by tags to be crammable, or if
	 * the old blockstate does not exist in the crammed block, the replacement does not occur.
	 * Use {@link removeState} to remove a blockstate from the crammed block.
	 * @param oldState The state to replace
	 * @param newState The state oldState is to be replaced with
	 * @param simulate If true, does not actually remove or add any states; the return value is not affected.
	 * @return true if the replacement occurred or would have occurred.
	 */
	public boolean replaceState(BlockState oldState, BlockState newState, boolean simulate);
	
	/**
	 * Attemps to remove an existing blockstate from the crammed block.
	 * If the given state does not exist in the crammed block, nothing will occur.
	 * @param oldState
	 * @param simulate If true, does not actually remove the state; the return value is unaffected.
	 * @return true if the state was or would have been successfully removed
	 */
	public boolean removeState(BlockState oldState, boolean simulate);
	
	
}
