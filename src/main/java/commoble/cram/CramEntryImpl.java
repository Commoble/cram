package commoble.cram;

import commoble.cram.api.CramEntry;
import commoble.cram.api.functions.EntityCollisionBehavior;
import commoble.cram.api.functions.LightGetter;
import commoble.cram.api.functions.NaiveVoxelProvider;
import commoble.cram.api.functions.ScheduledTickBehavior;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.RayTraceContext.IVoxelProvider;

public class CramEntryImpl implements CramEntry
{
	private final Block block;
	
	// passive properties -- generally safe to defer to the block
	public LightGetter lightGetter = BlockState::getLightValue;
	public IVoxelProvider shapeGetter = AbstractBlockState::getShape;
	public IVoxelProvider collisionShapeGetter = AbstractBlockState::getCollisionShape;
	public IVoxelProvider cameraShapeGetter = AbstractBlockState::getRaytraceShape;
	public NaiveVoxelProvider raytraceShapeGetter = AbstractBlockState::getRayTraceShape;
	public NaiveVoxelProvider renderShapeGetter = AbstractBlockState::getRenderShapeTrue;
	public NaiveVoxelProvider attachmentShapeGetter = AbstractBlockState::getRenderShape; // mcp misname
	
	// active behaviors -- must be explicitly defined
	public EntityCollisionBehavior entityCollisionBehavior = EntityCollisionBehavior.NOPE;
	public ScheduledTickBehavior scheduledTickBehavior = ScheduledTickBehavior.NOPE;
	
	public CramEntryImpl(Block block)
	{
		this.block = block;
	}
	
	public Block getBlock()
	{
		return this.block;
	}

	@Override
	public CramEntry setLightGetter(LightGetter lightGetter)
	{
		this.lightGetter = lightGetter;
		return this;
	}

	@Override
	public CramEntry setShapeGetter(IVoxelProvider shapeGetter)
	{
		this.shapeGetter = shapeGetter;
		return this;
	}

	@Override
	public CramEntry setCollisionShapeGetter(IVoxelProvider shapeGetter)
	{
		this.collisionShapeGetter = shapeGetter;
		return this;
	}

	@Override
	public CramEntry setRenderShapeGetter(NaiveVoxelProvider shapeGetter)
	{
		this.renderShapeGetter = shapeGetter;
		return this;
	}

	@Override
	public CramEntry setRaytraceShapeGetter(NaiveVoxelProvider shapeGetter)
	{
		this.raytraceShapeGetter = shapeGetter;
		return this;
	}
	
	@Override
	public CramEntry setEntityCollisionBehavior(EntityCollisionBehavior behavior)
	{
		this.entityCollisionBehavior = behavior;
		return this;
	}
	
	@Override
	public CramEntry setScheduledTickBehavior(ScheduledTickBehavior behavior)
	{
		this.scheduledTickBehavior = behavior;
		return this;
	}

}
