package com.github.commoble.cram.vanilla;

import java.util.Random;

import com.github.commoble.cram.api.CramAccessor;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PressurePlateBehaviors
{
	public static void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CramAccessor cram)
	{
		if (!world.isRemote)
		{
			Block block = state.getBlock();
			if (block instanceof AbstractPressurePlateBlock)
			{
				AbstractPressurePlateBlock plateBlock = (AbstractPressurePlateBlock)block;
				int oldPower = PressurePlateHelper.getRedstoneStrength(plateBlock, state);
				if(oldPower == 0)
				{
					updateState(plateBlock, state, world, pos, oldPower, cram);
				}
			}
		}
	}
	
	public static void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CramAccessor cram)
	{
		Block block = state.getBlock();
		if (block instanceof AbstractPressurePlateBlock)
		{
			AbstractPressurePlateBlock plateBlock = (AbstractPressurePlateBlock)block;
			int oldPower = PressurePlateHelper.getRedstoneStrength(plateBlock, state);
			if(oldPower > 0)
			{
				updateState(plateBlock, state, world, pos, oldPower, cram);
			}
		}
	}
	
	public static void updateState(AbstractPressurePlateBlock block, BlockState oldState, World world, BlockPos pos, int oldPower, CramAccessor cram)
	{
		int newPower = PressurePlateHelper.computeRedstoneStrength(block, world, pos);
		boolean wasPowered = oldPower > 0;
		boolean isPowered = newPower > 0;
		if (newPower != oldPower)
		{
			BlockState newState = PressurePlateHelper.getBlockStateForPower(block, oldState, newPower);
			cram.replaceState(oldState, newState, false);
			world.notifyNeighborsOfStateChange(pos, block);
			world.notifyNeighborsOfStateChange(pos.down(), block);
			// pressure plates in normal worlds call world.markBlockRangeForRenderUpdate
			// but that only does things on clients and plate states can only be updated on the server
			// so the call does nothing and we'll omit it here
			if (wasPowered && !isPowered)
			{
				PressurePlateHelper.playClickOffSound(block, world, pos);
			}
			else if (!wasPowered && isPowered)
			{
				PressurePlateHelper.playClickOnSound(block, world, pos);
			}
			
			if (isPowered)
			{
				cram.scheduleTick(newState, block.tickRate(world));
			}
		}
	}
}
