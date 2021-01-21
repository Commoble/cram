package commoble.cram;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockPos;

public class CramItemUseContext extends BlockItemUseContext
{
	public final BlockPos cramPos;
	
	public CramItemUseContext(ItemUseContext context, BlockPos cramPos)
	{
		super(context);
		this.cramPos = cramPos;
	}
	
	@Override
	public BlockPos getPos()
	{
		return this.cramPos;
	}
}
