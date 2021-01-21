package com.github.commoble.cram.api.functions;

import com.github.commoble.cram.api.CramEntry;

import net.minecraft.block.Block;

public interface CramRegistrator
{
	/**
	 * Gets the cram entry for a Block instance so that its properties can be adjusted
	 * @param block The block instance whose cram entry needs to be adjusted
	 * @return A cram entry that can be adjusted
	 */
	public CramEntry getCramEntry(Block block);
	
}
