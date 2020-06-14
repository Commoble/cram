package com.github.commoble.cram;

import java.util.Collection;
import java.util.Set;

import com.github.commoble.cram.api.CramAccessor;
import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public class CramBlockAccessor implements CramAccessor
{
	public CrammedTileEntity te;
	
	public CramBlockAccessor(CrammedTileEntity te)
	{
		this.te = te;
	}
	
	@Override
	public Collection<BlockState> getBlockStates()
	{
		return this.te.states;
	}

	@Override
	public boolean canStateBeAdded(BlockState state, BlockState... ignoreStates)
	{
		World world = this.te.getWorld();
		BlockPos pos = this.te.getPos();
		Set<BlockState> ignorableStates = Sets.newHashSet(ignoreStates);
		return !this.te.states.contains(state)
			&& CramTags.isBlockCrammingPermittedByTags(state.getBlock())
			&& this.te.states.stream()
				.allMatch(stateInTe -> ignorableStates.contains(stateInTe) ||
				VoxelShapes.combineAndSimplify(stateInTe.getShape(world, pos), state.getShape(world, pos), IBooleanFunction.AND).isEmpty());
	}

	@Override
	public boolean addState(BlockState state, boolean simulate)
	{
		if (this.canStateBeAdded(state))
		{
			if (!simulate)
			{
				this.te.addBlockStatesAndUpdate(state);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean replaceState(BlockState oldState, BlockState newState, boolean simulate)
	{
		if (this.canStateBeAdded(newState, oldState))
		{
			if (this.removeState(oldState, simulate))
			{
				if (!simulate)
				{
					this.te.addBlockStatesAndUpdate(newState);
				}
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean removeState(BlockState oldState, boolean simulate)
	{
		if (this.te.states.contains(oldState))
		{
			if (!simulate)
			{
				this.te.states.remove(oldState);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

}
