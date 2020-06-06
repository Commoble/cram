package com.github.commoble.cram;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.math.RayTraceContext.IVoxelProvider;

public class CrammableBlocks
{
	public static final Map<Block, IVoxelProvider> REGISTRY = new HashMap<>();
	
	public static void register(Block block, IVoxelProvider provider)
	{
		REGISTRY.put(block, provider);
	}

	/**
	 * Register a block to the crammable blocks registry, using its default getShape method for the voxel provider
	 * @param block
	 */
	@SuppressWarnings("deprecation")
	public static void register(Block block)
	{
		REGISTRY.put(block, block::getShape);
	}
}
