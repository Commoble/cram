package commoble.cram.api.functions;

import javax.annotation.Nullable;

import commoble.cram.api.CramReader;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface RedstoneConnectivityPredicate
{
	/**
	 * Interface representing a function that returns true if a substate of a cram block can connect to an adjacent redstone block
	 * @param state A blockstate in a cram block
	 * @param reader The world containing the cram block
	 * @param pos The position of the cram block
	 * @param side The side of the adjacent block trying to connect to this block,
	 * i.e. the direction pointing from an adjacent block to the cram block
	 * @param cramReader Gives read access to a cram block
	 * @return True if the adjacent block should be able to form a connection to this state
	 */
	public boolean test(BlockState state, IBlockReader reader, BlockPos pos, @Nullable Direction side, CramReader cramReader);
}
