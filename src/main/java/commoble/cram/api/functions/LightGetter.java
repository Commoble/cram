package com.github.commoble.cram.api.functions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

/**
 * Defines a function for getting a blockstate's light based on world context.
 * The method signature of Block::getLightValue
 */
@FunctionalInterface
public interface LightGetter
{
	/**
	 * 
	 * @param state The blockstate we want to know the light of. NOT necessarily the state at the given world position!
	 * @param world The world we're looking at
	 * @param pos The position we're looking at
	 * @return The light the given state would have if it were at that position
	 */
	public int getLight(BlockState state, IWorldReader world, BlockPos pos);
}
