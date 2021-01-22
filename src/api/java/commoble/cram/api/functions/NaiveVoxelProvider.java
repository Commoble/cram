package commoble.cram.api.functions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

/**
 * Function used for getting a blockstate's VoxelShape without selection context.
 * The method signature of getRaytraceShape and getRenderShape.
 */
@FunctionalInterface
public interface NaiveVoxelProvider
{
	/**
	 * 
	 * @param state The blockstate we want the shape of. Not necessarily the blockstate at the given world position.
	 * @param world The world we're looking at
	 * @param pos The position we're looking at
	 * @return The VoxelShape the blockstate would have if it were at the given position
	 */
	public VoxelShape get(BlockState state, IBlockReader world, BlockPos pos);
}
