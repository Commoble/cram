package commoble.cram.api;

import commoble.cram.api.functions.EntityCollisionBehavior;
import commoble.cram.api.functions.LightGetter;
import commoble.cram.api.functions.NaiveVoxelProvider;
import commoble.cram.api.functions.RandomTickBehavior;
import commoble.cram.api.functions.RedstoneConnectivityPredicate;
import commoble.cram.api.functions.RedstonePowerProvider;
import commoble.cram.api.functions.ScheduledTickBehavior;
import net.minecraft.util.math.RayTraceContext.IVoxelProvider;

public interface CramEntry
{
	/**
	 * Set the function used to determine light value of a crammed blockstate.
	 * Defaults to AbstractBlockState::getLightValue if not set here.
	 * @param lightGetter The light-getting function to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setLightGetter(final LightGetter lightGetter);
	
	/**
	 * Sets the function used to determine the primary VoxelShape of a crammed blockstate.
	 * The primary shape of a blockstate determines the outline rendered on the client.
	 * Collision shapes and render shapes default to the primary shape when unspecified.
	 * This is also the shape used to determine whether two blockstates would overlap in the crammed block.
	 * Defaults to the block's getShape method if not set here.
	 * @param shapeGetter The shape getter to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setInteractionShapeGetter(final IVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the collision shape of a crammed blockstate.
	 * The collision shape is used for handling entity collisions, and determining how big a block's
	 * side is for placing other blocks like torches against it.
	 * Defaults to the block's getCollisionShape(BlockState, IBlockReader, BlockPos) method if not set here.
	 * @param shapeGetter The shape getter to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setCollisionShapeGetter(final IVoxelProvider shapeGetter);

	/**
	 * Sets the function used to determine the camera shape of the block.
	 * Used for determining the collision shape used to block the camera in third-person camera mode.
	 * Defaults to the block's getRayTraceShape(BlockState, IBlockReader, BlockPos, ISelectionContext) method
	 * if not set here.
	 * @param shapeGetter The shape getter to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setCameraShapeGetter(final IVoxelProvider shapeGetter);

	/**
	 * Sets the function used to determine the raytrace shape of a crammed blockstate.
	 * Defaults to the block's getRaytraceShape(BlockState, IBlockReader, BlockPos) method if not set here.
	 * 
	 * This is what minecraft uses the raytrace shape for:
	 * The raytrace shape is rarely used; ::getShape or ::getCollisionShape is used to determine the hit position of raytraces.
	 * If the aforementioned shape AND the raytrace shape both exist, and are both hit by a raytrace,
	 * and the raytrace shape is hit before the primary shape,
	 * then the raytrace result will contain the Direction of the face of the raytrace shape hit,
	 * but still use the position hit on the primary shape.
	 * @param shapeGetter The shape getter to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setRaytraceShapeGetter(final NaiveVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the render shape of a crammed blockstate.
	 * The render shape is used for determining whether to cull faces of adjacent blocks.
	 * It's also used to determine whether a block is an "opaque cube", which in turn is used
	 * for a lot of miscellaneous things not related to rendering.
	 * Defaults to the block's getRenderShape method if not set here.
	 * @param shapeGetter The shape getter to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setRenderShapeGetter(final NaiveVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the attachment shape of the block.
	 * Used to determine whether torches and similar things can be attached to a face of a block.
	 * Defaults to the block's getCollisionShape(BlockState, IBlockReader, BlockPos) method if not set here.
	 * @param shapeGetter The shape getter to use for the crammed blockstate
	 * @return this
	 */
	public CramEntry setAttachmentShapeGetter(final NaiveVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the insulation shape of the block.
	 * Redstone substates can use the insulation shape in the cram block to help them decide
	 * whether they can connect to adjacent redstone blocks.
	 * This defaults to the block's getShape method if the given state cannot provide power, or an empty shape if it can.
	 * @param shapeGetter
	 * @return
	 */
	public CramEntry setInsulationShapeGetter(final NaiveVoxelProvider shapeGetter);
	
	/**
	 * Sets the function used to determine the redstone connectivity getter of the block.
	 * @param getter The function to determine whether redstone-connectable blocks can connect on the given side.
	 * The function given should have no side-effects.
	 * Be aware that the side given may be null, and represents the side of the *adjacent* block trying to make the connection
	 * (i.e. the direction pointing from the adjacent block toward this block)
	 * @return this
	 */
	public CramEntry setRedstoneConnectivityGetter(final RedstoneConnectivityPredicate getter);
	
	/**
	 * Sets the function used to determine the "weak" redstone power of a blockstate given sided worldpos context
	 * (the power that can't be conducted through a solid block)
	 * @param getter A function that determines redstone power given a world context.
	 * Be aware that the side given is the side of the adjacent block receiving power
	 * (the direction pointing from the adjacent block to the cram block)
	 * @return this
	 */
	public CramEntry setWeakPowerGetter(final RedstonePowerProvider getter);

	
	/**
	 * Sets the function used to determine the "strong" redstone power of a blockstate given sided worldpos context
	 * (the power that can be conducted through a solid block)
	 * @param getter A function that determines redstone power given a world context.
	 * Be aware that the side given is the side of the adjacent block receiving power
	 * (the direction pointing from the adjacent block to the cram block)
	 * @return this
	 */
	public CramEntry setStrongPowerGetter(final RedstonePowerProvider getter);
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
	 * @param behavior The function to run on scheduled ticks
	 * @return this
	 */
	public CramEntry setScheduledTickBehavior(final ScheduledTickBehavior behavior);
	
	/**
	 * Sets the function that determines when a random tick occurs for a component blockstate.
	 * Currently, this will be called for all substates in a cram block whenever the cram block is ticked.
	 * This MUST be set manually if behavior is desired.
	 * @param behavior The function to run on random ticks
	 * @return this
	 */
	public CramEntry setRandomTickBehavior(final RandomTickBehavior behavior);
}
