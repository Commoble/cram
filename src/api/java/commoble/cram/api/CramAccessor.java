package commoble.cram.api;

import net.minecraft.block.BlockState;

/**
 * This interface permits limited read/write access to a crammed block.
 * you can get a reference to the capability provider via

 */
public interface CramAccessor extends CramReader
{
	
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
	
	/**
	 * Schedules a blockstate inside the cram block to be ticked.
	 * @param state The state to tick
	 * @param delay How long to wait before ticking it
	 */
	public void scheduleTick(BlockState state, int delay);
}
