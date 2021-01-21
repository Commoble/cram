package commoble.cram;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistrar
{	
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Cram.MODID);
	public static final Material CRAMMED_BLOCK_MATERIAL = new Material(MaterialColor.WHITE_TERRACOTTA, false, true, true, false, true, false, false, PushReaction.BLOCK); 
	
	public static final RegistryObject<CrammedBlock> CRAMMED_BLOCK = BLOCKS.register(Names.CRAMMED_BLOCK, ()->
		new CrammedBlock(Block.Properties.create(CRAMMED_BLOCK_MATERIAL).hardnessAndResistance(0.1F).notSolid()));
}
