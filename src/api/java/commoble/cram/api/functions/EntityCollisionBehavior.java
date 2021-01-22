package commoble.cram.api.functions;

import commoble.cram.api.CramAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface EntityCollisionBehavior
{
	/** Default entity collision behavior **/
	public static final EntityCollisionBehavior NOPE = (state, world, pos, entity, access) -> {};
	
	/**
	 * 
	 * @param state The state that an entity collided with
	 * @param world
	 * @param pos The position of the crammed block
	 * @param entity The entity that the state collided with
	 * @param access Access to the crammed block to adjust its states
	 */
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CramAccessor access);
}
