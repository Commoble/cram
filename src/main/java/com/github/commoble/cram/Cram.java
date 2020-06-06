package com.github.commoble.cram;

import com.github.commoble.cram.client.ClientEvents;

import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
		CrammableBlocks.register(Blocks.STONE_PRESSURE_PLATE);
		CrammableBlocks.register(Blocks.TORCH);
		CrammableBlocks.register(Blocks.WALL_TORCH);
	}
	
	private static void addForgeListeners(IEventBus forgeBus)
	{
		forgeBus.addListener(Cram::onTestStick);
	}
	
	private static void onTestStick(PlayerInteractEvent.RightClickBlock event)
	{
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		if (!event.getWorld().isRemote && stack.getItem() == Items.STICK)
		{
			event.getWorld().setBlockState(event.getPos(), BlockRegistrar.CRAMMED_BLOCK.get().getDefaultState());
			CrammedTileEntity.addBlockStates(event.getWorld(), event.getPos(),
				Blocks.STONE_PRESSURE_PLATE.getDefaultState(),
				Blocks.WALL_TORCH.getDefaultState(),
				Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.SOUTH));
		}
	}
	
	public static ResourceLocation getModRL(String name)
	{
		return new ResourceLocation(MODID, name);
	}
}
