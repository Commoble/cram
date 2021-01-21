package commoble.cram;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityRegistrar
{
	public static final DeferredRegister<TileEntityType<?>> TE_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Cram.MODID);

	public static final RegistryObject<TileEntityType<CrammedTileEntity>> CRAMMED_BLOCK = TE_TYPES.register(Names.CRAMMED_BLOCK, () ->
		TileEntityType.Builder.create(CrammedTileEntity::new, BlockRegistrar.CRAMMED_BLOCK.get()).build(null));
}
