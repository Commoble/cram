package com.github.commoble.cram.client;

import com.github.commoble.cram.TileEntityRegistrar;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents
{
	/** Called from mod constructor when we're on the client **/
	public static void addClientListeners(IEventBus modBus, IEventBus forgeBus)
	{
		addModListeners(modBus);
		addForgeListeners(forgeBus);
	}
	
	private static void addModListeners(IEventBus modBus)
	{
		modBus.addListener(ClientEvents::onClientSetup);
	}
	
	private static void addForgeListeners(IEventBus forgeBus)
	{
	}
	
	private static void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntityRenderer(TileEntityRegistrar.CRAMMED_BLOCK.get(), CrammedTileEntityRenderer::new);
	}
}
