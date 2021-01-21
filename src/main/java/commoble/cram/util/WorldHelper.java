package commoble.cram.util;

import java.util.List;

import javax.annotation.Nullable;

import commoble.cram.AirSimulator;
import commoble.cram.CramTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.CramBlockItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class WorldHelper
{
	public static <T> LazyOptional<T> getTileCapability(IBlockReader world, BlockPos pos, Capability<T> cap)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
		{
			return te.getCapability(cap);
		}
		else
		{
			return LazyOptional.empty();
		}
	}
	/**
	 * Gets the blockstate that we could place into a block if that block was air, even if it isn't air
	 * Returns null if we can't place a block here
	 * @param context
	 * @return
	 */
	@Nullable
	public static BlockState getBlockStateToCram(BlockItemUseContext context)
	{
		ItemStack stack = context.getItem();
		Item item = stack.getItem();
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		if (item instanceof BlockItem && CramTags.isItemCrammingPermittedByTags(item))
		{
			BlockItem blockItem = (BlockItem)item;
			BlockState state = CramBlockItemHelper.getStateForPlacement(blockItem, context);
			if (state != null && state.isValidPosition(new AirSimulator(world), pos))
			{
				return state;
			}
		}
		
		return null;
	}
	
	public static boolean isRoomForState(IBlockReader world, BlockPos pos, BlockState state)
	{
		VoxelShape existingShape = world.getBlockState(pos).getShape(world, pos);
		VoxelShape newShape = state.getShape(world, pos);

		List<AxisAlignedBB> oldBoxes = existingShape.toBoundingBoxList();
		List<AxisAlignedBB> newBoxes = newShape.toBoundingBoxList();
		
		for (AxisAlignedBB oldBox : oldBoxes)
		{
			for (AxisAlignedBB newBox : newBoxes)
			{
				if (oldBox.intersects(newBox))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
