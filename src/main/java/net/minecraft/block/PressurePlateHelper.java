package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PressurePlateHelper
{
	public static void playClickOnSound(AbstractPressurePlateBlock block, World world, BlockPos pos)
	{
		block.playClickOnSound(world, pos);
	}
	
	public static void playClickOffSound(AbstractPressurePlateBlock block, World world, BlockPos pos)
	{
		block.playClickOffSound(world, pos);
	}
	
	public static int getRedstoneStrength(AbstractPressurePlateBlock block, BlockState state)
	{
		return block.getRedstoneStrength(state);
	}
	
	public static int computeRedstoneStrength(AbstractPressurePlateBlock block, World world, BlockPos pos)
	{
		return block.computeRedstoneStrength(world, pos);
	}
	
	public static BlockState getBlockStateForPower(AbstractPressurePlateBlock block, BlockState oldState, int newPower)
	{
		return block.setRedstoneStrength(oldState, newPower);
	}
	
	public static int getPoweredDuration(AbstractPressurePlateBlock block)
	{
		return block.getPoweredDuration();
	}
}
