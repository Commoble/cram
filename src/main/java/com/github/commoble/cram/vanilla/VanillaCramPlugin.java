package com.github.commoble.cram.vanilla;

import com.github.commoble.cram.Cram;
import com.github.commoble.cram.api.AutoCramPlugin;
import com.github.commoble.cram.api.CramPlugin;
import com.github.commoble.cram.api.functions.CramRegistrator;

import net.minecraft.block.Blocks;

@AutoCramPlugin
public class VanillaCramPlugin implements CramPlugin
{

	@Override
	public void register(CramRegistrator registry)
	{
		registry.getCramEntry(Blocks.STONE_PRESSURE_PLATE)
			.setEntityCollisionBehavior(PressurePlateBehaviors::onEntityCollision)
			.setScheduledTickBehavior(PressurePlateBehaviors::onScheduledTick);
		
		Cram.LOGGER.info("Loaded Cram Plugin for vanilla objects");
	}

}
