package commoble.cram;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class CramTags
{
	/** BlockItems that can be crammed into existing crammable blocks **/
	public static final ITag<Item> ALLOWED_ITEMS = ItemTags.makeWrapperTag("cram:allowed");
	/** Items that cannot be crammed into existing crammable blocks. Overrides allowed items. **/
	public static final ITag<Item> DENIED_ITEMS = ItemTags.makeWrapperTag("cram:denied");
	
	/** Blocks that can have items crammed into them **/
	public static final ITag<Block> ALLOWED_BLOCKS = BlockTags.makeWrapperTag("cram:allowed");
	/** Blocks that cannot have items crammed into them. Overrides allowed blocks. **/
	public static final ITag<Block> DENIED_BLOCKS = BlockTags.makeWrapperTag("cram:denied");
	
	/** Blocks that are made for holding crammed blocks in. Cannot be crammed into other blocks themselves.**/
	public static final ITag<Block> CRAMMED_BLOCKS = BlockTags.makeWrapperTag("cram:crammed_blocks");

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
