package com.github.commoble.cram;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MixinCallbacks
{
	public static void onBlockItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> info)
	{
		boolean cramSuccessful = false;
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		if (player != null && world != null && player.isSneaking())
		{
			Hand hand = context.getHand();
			hand = hand == null ? Hand.MAIN_HAND : hand;
			ItemStack stack = player.getHeldItem(hand);
			Item item = stack.getItem();
			if (item instanceof BlockItem && CramTags.isItemCrammingPermittedByTags(item))
			{
				BlockPos hitPos = context.getPos();
				BlockState hitState = world.getBlockState(hitPos);
				Block hitBlock = hitState.getBlock();
				
				// try to find a place to cram the block
				BlockPos targetPos = null;
				BlockState targetState = null;
				if (CramTags.isBlockCrammingPermittedByTags(hitBlock))
				{
					targetPos = hitPos;
					targetState = hitState;
				}
				else
				{
					BlockPos adjacentPos = hitPos.offset(context.getFace());
					BlockState adjacentState = world.getBlockState(adjacentPos);
					Block adjacentBlock = adjacentState.getBlock();
					

					if (CramTags.isBlockCrammingPermittedByTags(adjacentBlock))
					{
						targetPos = adjacentPos;
						targetState = adjacentState;
					}
				}
				
				// if we found a place to cram the block
				if (targetPos != null && targetState != null)
				{
					BlockItemUseContext blockItemContext = new CramItemUseContext(context, targetPos);
					
					BlockState newState = WorldHelper.getBlockStateToCram(blockItemContext);
					if (newState != null && WorldHelper.isRoomForState(world, targetPos, newState))
					{
						// blocks should only be set on the server, but the result of the method should be the same
						if (!world.isRemote)
						{
							CrammedTileEntity.addBlockStates(world, targetPos, targetState, newState);
						}
						SoundType soundtype = newState.getSoundType(world, targetPos, context.getPlayer());
						world.playSound(player, targetPos, newState.getSoundType(world, targetPos, player).getPlaceSound(), SoundCategory.BLOCKS,
							(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
						stack.shrink(1);
						cramSuccessful = true;
					}
				}
			}
		}
		
		// if we crammed any blocks, return early from the original onItemUse method before it does anything else
		if (cramSuccessful)
		{
			info.setReturnValue(ActionResultType.SUCCESS);
		}
	}
}
