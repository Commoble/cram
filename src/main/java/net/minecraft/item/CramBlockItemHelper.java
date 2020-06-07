package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

/** Cheap trick for accessing protected methods in BlockItem **/
public abstract class CramBlockItemHelper extends BlockItem
{

	public CramBlockItemHelper(Block blockIn, Properties builder)
	{
		super(blockIn, builder);
	}

	public static BlockState getStateForPlacement(BlockItem blockItem, BlockItemUseContext context)
	{
		return blockItem.getStateForPlacement(context);
	}
}
