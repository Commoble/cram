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
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Cram.MODID);
	public static final Material CRAMMED_BLOCK_MATERIAL = new Material(MaterialColor.WHITE_TERRACOTTA,
		false,	// is liquid == false
		false,	// is solid == false -- crammed blocks are never going to be opaque cubes
		true,	// blocks movement == true -- crammed blocks *can* block movement if the sublocks do
		false,	// is opaque == false -- crammed blocks are never going to be opaque cubes
		false,	// flammable == false -- note that this is only used by LAVA, not fire. False for now,
		// might make a flammable material later, but that would require making an extra cramblock instance
		false,	// replaceable == false
		PushReaction.BLOCK); 
	
	public static final RegistryObject<CrammedBlock> CRAMMED_BLOCK = BLOCKS.register(Names.CRAMMED_BLOCK, ()->
		new CrammedBlock(Block.Properties.create(CRAMMED_BLOCK_MATERIAL).hardnessAndResistance(0.1F).notSolid()));
}
