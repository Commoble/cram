package com.github.commoble.cram;

import com.github.commoble.cram.api.CramEntry;
import com.github.commoble.cram.api.functions.EntityCollisionBehavior;
import com.github.commoble.cram.api.functions.LightGetter;
import com.github.commoble.cram.api.functions.NaiveVoxelProvider;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.RayTraceContext.IVoxelProvider;

public class CramEntryImpl implements CramEntry
{
	private final Block block;
	
	public LightGetter lightGetter = BlockState::getLightValue;
	public IVoxelProvider shapeGetter = BlockState::getShape;
	public IVoxelProvider collisionShapeGetter = BlockState::getCollisionShape;
	public NaiveVoxelProvider renderShapeGetter = BlockState::getRenderShape;
	public NaiveVoxelProvider raytraceShapeGetter = BlockState::getRaytraceShape;
	public EntityCollisionBehavior entityCollisionBehavior = EntityCollisionBehavior.NOPE;
	
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

}
