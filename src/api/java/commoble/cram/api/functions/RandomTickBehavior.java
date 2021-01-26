package commoble.cram.api.functions;

import java.util.Random;

import commoble.cram.api.CramAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface RandomTickBehavior
{
	/** Default scheduled tick behavior **/
	public static final RandomTickBehavior NOPE = (state, world, pos, random, access) -> {};
	
	/**
	 * Function that will be run for substates of a cram block when the cram block is randomly ticked.
	 * Only blockstates marked as able to receive random ticks via their blockproperties will be ticked.
	 * @param stateIn The substate in the cram block
	 * @param worldIn The server world containing the cram block.
	 * @param pos The blockpos of the cram block.
	 * @param random The RNG given to the cram block.
	 * @param cramAccess The cram accessor to manipulate the cram block's states, if needed.
	 */
	public void onRandomTick(BlockState stateIn, ServerWorld worldIn, BlockPos pos, Random random, CramAccessor cramAccess);
}
