package com.github.commoble.cram.api;

import net.minecraft.block.BlockState;

/**
 * This interface permits limited access to a crammed block.
 */
public interface CramAccess
{
	/**
	 * Returns whether the crammed block contains the given state
	 * @param state
	 * @param ignoreState
	 * @return
	 */
	public boolean containsState(BlockState state);

	/**
	 * Returns whether there is room for a given blockstate to be added without overlapping.
	 * @param state The state to check for whether sufficient room exists to add
	 * @param ignoreStates Optional states that can be ignored when checking whether room for the other state exists
	 * @return Whether there is room for the given state
	 */
	public boolean isRoomForState(BlockState state, BlockState... ignoreStates);
	
	/**
	 * Attempt to add a blockstate to a crammed block.
	 * If there is no room for the state, or the new state is not permitted to be crammed by block tags,
	 * then the state will not be added.
	 * @param state
	 * @return true if the state was successfully added
	 */
	public boolean addState(BlockState state);
	
	/**
	 * Attempts to replace an existing blockstate with a new blockstate.
	 * If the new blockstate is not allowed by tags to be crammable, or if
	 * the old blockstate does not exist in the crammed block, the replacement does not occur.
	 * Use {@link removeState} to remove a blockstate from the crammed block.
	 * @param oldState
	 * @param newState
	 * @return true if the replacement occurred.
	 */
	public boolean replaceState(BlockState oldState, BlockState newState);
	
	/**
	 * Attemps to remove an existing blockstate from the crammed block.
	 * If the given state does not exist in the crammed block, nothing else will occur.
	 * @param oldState
	 * @param playSound Whether to play the block-break sound if the removal was successful
	 * @param dropItems Whether to drop the old blockstate as an item if the removal was successful
	 * @return true if the state was successfully removed
	 */
	public boolean removeState(BlockState oldState, boolean playSound, boolean dropItems);
	
	
}
