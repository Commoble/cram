package com.github.commoble.cram.client;

import com.github.commoble.cram.BlockRegistrar;
import com.github.commoble.cram.CrammableBlocks;
import com.github.commoble.cram.TileEntityRegistrar;
import com.github.commoble.cram.WorldHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents
{
	/** Called from mod constructor when we're on the client **/
	public static void addClientListeners(IEventBus modBus, IEventBus forgeBus)
	{
		ClientConfig.initClientConfig();

		addModListeners(modBus);
		addForgeListeners(forgeBus);
	}

	private static void addModListeners(IEventBus modBus)
	{
		modBus.addListener(ClientEvents::onClientSetup);
	}

	private static void addForgeListeners(IEventBus forgeBus)
	{
		forgeBus.addListener(ClientEvents::onHighlightBlock);
	}

	private static void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntityRenderer(TileEntityRegistrar.CRAMMED_BLOCK.get(), CrammedTileEntityRenderer::new);
	}

	public static void onHighlightBlock(DrawHighlightEvent.HighlightBlock event)
	{
		if (ClientConfig.INSTANCE.showPlacementPreview.get())
		{
			@SuppressWarnings("resource")
			ClientPlayerEntity player = Minecraft.getInstance().player;
			World world = player.world;
			if (player != null && world != null && player.isSneaking())
			{
				BlockRayTraceResult rayTrace = event.getTarget();
				BlockPos hitPos = rayTrace.getPos();
				BlockState hitState = world.getBlockState(hitPos);
				Hand hand = player.getActiveHand();
				hand = hand == null ? Hand.MAIN_HAND : hand;
				ItemStack stack = player.getHeldItem(hand); 
				Item item = stack.getItem();
				if (item instanceof BlockItem)
				{
					Block heldBlock = ((BlockItem) item).getBlock();
					if (CrammableBlocks.REGISTRY.containsKey(heldBlock))
					{
						Block hitBlock = hitState.getBlock();
						// if we can cram the block we're holding, and the block we're looking at can have more blocks crammed in it
						if (CrammableBlocks.REGISTRY.containsKey(hitBlock))
						{
							BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, hand, rayTrace));
							
							BlockState newState = WorldHelper.getBlockStateToCram(context);
							if (newState != null && WorldHelper.isRoomForState(world, hitPos, newState))
							{

								BlockPreviewRenderer.renderBlockPreview(hitPos, newState, world, event.getInfo().getProjectedView(), event.getMatrix(), event.getBuffers());

							}
						}
						else
						{
							BlockPos offsetPos = hitPos.offset(rayTrace.getFace());
							Block offsetBlock = world.getBlockState(offsetPos).getBlock();

							// if we can cram the block we're holding, and the block next to the block we're looking at can cram
							if (CrammableBlocks.REGISTRY.containsKey(offsetBlock) || offsetBlock == BlockRegistrar.CRAMMED_BLOCK.get())
							{
								BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, hand, rayTrace));
								
								BlockState newState = WorldHelper.getBlockStateToCram(context);
								if (newState != null && WorldHelper.isRoomForState(world, offsetPos, newState))
								{

									BlockPreviewRenderer.renderBlockPreview(offsetPos, newState, world, event.getInfo().getProjectedView(), event.getMatrix(), event.getBuffers());

								}
							}
						}
					}
				}
			}
		}
	}

}
