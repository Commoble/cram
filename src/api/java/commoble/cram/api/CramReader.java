package commoble.cram.api;

import java.util.Collection;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;

/**
 * Interface that permits limited read access to a cram block.
 */
public interface CramReader
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
	 * Returns the VoxelShape representing the space in the cram block that redstone power cannot connect through.
	 * (the vanilla shape types can be retrieved by just calling the appropriate getter from the cram blockstate itself)
	 * @return a voxelshape
	 */
	public VoxelShape getInsulatingShape();
}
