package commoble.cram.util;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;

public class BlockStateTick implements Comparable<BlockStateTick>
{
	public static final String TIME = "time";
	public static final String STATE = "state";
	
	public final long readyTime;
	public final BlockState state;
	
	public BlockStateTick(BlockState state, long readyTime)
	{
		this.state = state;
		this.readyTime = readyTime;
	}
	
	public CompoundNBT toNBT()
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.put(STATE, NBTUtil.writeBlockState(this.state));
		nbt.putLong(TIME, this.readyTime);
		return nbt;
	}
	
	public static BlockStateTick fromNBT(CompoundNBT nbt)
	{
		return new BlockStateTick(NBTUtil.readBlockState(nbt.getCompound(STATE)), nbt.getLong(TIME));
	}

	@Override
	public int compareTo(BlockStateTick other)
	{
		return (int)(this.readyTime - other.readyTime);
	}
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof BlockStateTick
			&& this.readyTime == ((BlockStateTick)other).readyTime
			&& this.state == ((BlockStateTick)other).state;
	}

	@Override
	public int hashCode()
	{
		return this.state.hashCode() + (int)(this.readyTime >> 32) + (int)(this.readyTime & 32);
	}
}
