package com.github.commoble.cram.api;

import net.minecraft.block.Block;

public interface CramRegistrator
{
	/**
	 * Registers a block as being crammable.
	 * @param block
	 */
	public CramEntry registerCrammableBlock(Block block);
	
}
