package com.github.commoble.cram;

import java.util.Collection;

import com.github.commoble.cram.api.CramAccessor;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Forge requires that we specify a default implementation factory and storage when
 * we register a capability, but our cramming capability requires world context and
 * the default implementation factory doesn't provide world context.
 * 
 * This isn't meant to be used for anything accept making the capability registrar happy.
 */
public class EmptyCramAccessor implements CramAccessor
{
	public EmptyCramAccessor() {}
	
	public static final Collection<BlockState> NO_STATES = ImmutableSet.of();

	@Override
	public Collection<BlockState> getBlockStates()
	{
		return NO_STATES;
	}

	@Override
	public boolean canStateBeAdded(BlockState state, BlockState... ignoreStates)
	{
		return false;
	}

	@Override
	public boolean addState(BlockState state, boolean simulate)
	{
		return false;
	}

	@Override
	public boolean replaceState(BlockState oldState, BlockState newState, boolean simulate)
	{
		return false;
	}

	@Override
	public boolean removeState(BlockState oldState, boolean simulate)
	{
		return false;
	}
	
	public static class Storage implements IStorage<CramAccessor>
	{

		@Override
		public INBT writeNBT(Capability<CramAccessor> capability, CramAccessor instance, Direction side)
		{
			return new CompoundNBT();
		}

		@Override
		public void readNBT(Capability<CramAccessor> capability, CramAccessor instance, Direction side, INBT nbt)
		{
		}
		
	}

}
