package commoble.cram.api.functions;

import java.util.Random;

import commoble.cram.api.CramAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface ScheduledTickBehavior
{
	/** Default scheduled tick behavior **/
	public static final ScheduledTickBehavior NOPE = (state, world, pos, random, access) -> {};
	
	/**
	 * 
	 * @param state The state that was ticked
	 * @param world The server world where the tick was scheduled (scheduled ticks do not occur on client)
	 * @param pos The blockpos that was ticked
	 * @param random random
	 * @param access Access to the crammed block to adjust its states
	 */
	public void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CramAccessor access);
}
