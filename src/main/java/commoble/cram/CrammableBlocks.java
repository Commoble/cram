package com.github.commoble.cram;

import java.util.HashMap;
import java.util.Map;

import com.github.commoble.cram.api.CramEntry;

import net.minecraft.block.Block;

public class CrammableBlocks
{
	private static final Map<Block, CramEntryImpl> REGISTRY = new HashMap<>();
	
	public static CramEntry getCramEntry(Block block)
	{
		return getCramEntryImpl(block);
	}
	
	public static CramEntryImpl getCramEntryImpl(Block block)
	{
		if (REGISTRY.containsKey(block))
		{
			return REGISTRY.get(block);
		}
		else
		{
			CramEntryImpl entry = new CramEntryImpl(block);
			REGISTRY.put(block, entry);
			return entry;
		}
	}
}
