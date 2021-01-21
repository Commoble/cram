package commoble.cram;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class CramTags
{
	/** BlockItems that can be crammed into existing crammable blocks **/
	public static final ItemTags.Wrapper ALLOWED_ITEMS = new ItemTags.Wrapper(new ResourceLocation("cram:allowed"));
	/** Items that cannot be crammed into existing crammable blocks. Overrides allowed items. **/
	public static final ItemTags.Wrapper DENIED_ITEMS = new ItemTags.Wrapper(new ResourceLocation("cram:denied"));
	
	/** Blocks that can have items crammed into them **/
	public static final BlockTags.Wrapper ALLOWED_BLOCKS = new BlockTags.Wrapper(new ResourceLocation("cram:allowed"));
	/** Blocks that cannot have items crammed into them. Overrides allowed blocks. **/
	public static final BlockTags.Wrapper DENIED_BLOCKS = new BlockTags.Wrapper(new ResourceLocation("cram:denied"));
	
	/** Blocks that are made for holding crammed blocks in. Cannot be crammed into other blocks themselves.**/
	public static final BlockTags.Wrapper CRAMMED_BLOCKS = new BlockTags.Wrapper(new ResourceLocation("cram:crammed_blocks"));

	/**
	 * Whether an item can be crammed into an existing block, forming a Crammed Block if it wasn't already.
	 * @param item
	 * @return true if the item is in the allowed tag but not the denied tag, false otherwise
	 */
	public static boolean isItemCrammingPermittedByTags(Item item)
	{
		return ALLOWED_ITEMS.contains(item) && !DENIED_ITEMS.contains(item);
	}
	
	/**
	 * Whether a block can have more stuff crammed into it, forming a Crammed Block.
	 * @param block
	 * @return true if a block is in the allowed tag but not the denied tag, false otherwise
	 */
	public static boolean isBlockCrammingPermittedByTags(Block block)
	{
		return ALLOWED_BLOCKS.contains(block) && !DENIED_BLOCKS.contains(block);
	}
}
