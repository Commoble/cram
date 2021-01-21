package commoble.cram.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CramAccessorCapability
{
	/**
	 * Capability provider for the CramAccessor capability.
	 * If you need a cramming capability from a tile entity (including the Crammed Block's tile entity)
	 * then you can use this to get the capability optional via te.getCapability(CramAccessorCapability.INSTANCE)
	 * 
	 * The "default instance" and storage instance obtained from this field are for a permanently
	 * and immutably empty blockstate set, so they generally shouldn't be used
	 */
	@CapabilityInject(CramAccessor.class)
	public static Capability<CramAccessor> INSTANCE = null;
}
