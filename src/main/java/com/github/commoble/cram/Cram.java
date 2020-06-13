package com.github.commoble.cram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.commoble.cram.client.ClientEvents;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;

@Mod(Cram.MODID)
public class Cram
{
	public static final String MODID = "cram";
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	public Cram()
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		addModListeners(modBus);
		addForgeListeners(forgeBus);
		
		// make sure we are on the client before doing client stuff,
		// and do it in a separate class so java doesn't try to classload client classes on dedicated servers
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			ClientEvents.addClientListeners(modBus, forgeBus);
		}
	}
	
	private static void addModListeners(IEventBus modBus)
	{
		modBus.addListener(Cram::onCommonSetup);
		
		DeferredRegister<?>[] registers = {
			BlockRegistrar.BLOCKS,
			ItemRegistrar.ITEMS,
			TileEntityRegistrar.TE_TYPES
		};
		
		for(DeferredRegister<?> register : registers)
		{
			register.register(modBus);
		}
	}
	
	private static void onCommonSetup(FMLCommonSetupEvent event)
	{		
		// init API plugins
		Plugins.loadPlugins();
	}
	
	private static void addForgeListeners(IEventBus forgeBus)
	{
	}
	
	public static ResourceLocation getModRL(String name)
	{
		return new ResourceLocation(MODID, name);
	}
}
