package commoble.cram.api;

import commoble.cram.api.functions.EntityCollisionBehavior;
import commoble.cram.api.functions.LightGetter;
import commoble.cram.api.functions.NaiveVoxelProvider;
import commoble.cram.api.functions.ScheduledTickBehavior;
import net.minecraft.util.math.RayTraceContext.IVoxelProvider;

public interface CramEntry
{
	/**
	 * Set the function used to determine light value of a crammed blockstate.
	 * Defaults to Block::getLightValue if not set here.
	 * @param lightGetter
	 * @return this
	 */
	public CramEntry setLightGetter(final LightGetter lightGetter);
	
	/**
	 * Sets the function used to determine the primary VoxelShape of a crammed blockstate.
	 * The primary shape of a blockstate determines the outline rendered on the client.
	 * Collision shapes and render shapes default to the primary shape when unspecified.
	 * This is also the shape used to determine whether two blockstates would overlap in the crammed block.
	 * Defaults to Block::getShape if not set here.
	 * @param shapeGetter
	 * @return this
	 */
	public CramEntry setShapeGetter(final IVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the collision shape of a crammed blockstate.
	 * The collision shape is used for handling entity collisions, and determining how big a block's
	 * side is for placing other blocks like torches against it.
	 * Defaults to Block::getCollisionShape if not set here.
	 * @param shapeGetter
	 * @return this
	 */
	public CramEntry setCollisionShapeGetter(final IVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the render shape of a crammed blockstate.
	 * The render shape is used for determining whether to cull faces of adjacent blocks.
	 * It's also used to determine whether a block is an "opaque cube", which in turn is used
	 * for a lot of miscellaneous things not related to rendering.
	 * Defaults to Block::getRenderShape if not set here
	 * @param shapeGetter
	 * @return
	 */
	public CramEntry setRenderShapeGetter(final NaiveVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the raytrace shape of a crammed blockstate.
	 * Defaults to Block::getRaytraceShape if not set here.
	 * 
	 * This is what minecraft uses the raytrace shape for:
	 * The raytrace shape is rarely used; ::getShape or ::getCollisionShape is used to determine the hit position of raytraces.
	 * If the aforementioned shape AND the raytrace shape both exist, and are both hit by a raytrace,
	 * and the raytrace shape is hit before the primary shape,
	 * then the raytrace result will contain the Direction of the face of the raytrace shape hit,
	 * but still use the position hit on the primary shape.
	 * @param shapeGetter
	 * @return this
	 */
	public CramEntry setRaytraceShapeGetter(final NaiveVoxelProvider shapeGetter);
	
	/**
	 * Sets the function that determines what happens when an entity collides with a block.
	 * Does nothing by default. This MUST be set manually if behavior is desired.
	 * @param behavior
	 * @return this
	 */
	public CramEntry setEntityCollisionBehavior(final EntityCollisionBehavior behavior);
	
	/**
	 * Sets the function that determines what happens when a tick scheduled through the cram access
	 * occurs for a component blockstate. This MUST be set manually if behavior is desired.
	 * @param behavior
	 * @return this
	 */
	public CramEntry setScheduledTickBehavior(final ScheduledTickBehavior behavior);
}
