package commoble.cram.api.functions;

import commoble.cram.api.CramReader;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface RedstonePowerProvider
{
	/**
	 * Interface representing a function that returns a redstone power value given a sided cram block context
	 * @param state A substate in the cram block
	 * @param world The world the cram block is in
	 * @param pos The position of the cram block in that world
	 * @param side The side of a block adjacent to the cram block,
	 * i.e. the direction from the adjacent block to the cram block
	 * @param cramReader Read access to the cram block data
	 * @return An integer in the range [0,15] representing the power output of the given substate
	 */
	public int getValue(BlockState state, IBlockReader world, BlockPos pos, Direction side, CramReader cramReader);
}
