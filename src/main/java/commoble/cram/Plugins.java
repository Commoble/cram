package commoble.cram;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import org.objectweb.asm.Type;

import commoble.cram.api.AutoCramPlugin;
import commoble.cram.api.CramPlugin;
import commoble.cram.api.functions.CramRegistrator;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;

public class Plugins
{
	public static final Comparator<AnnotationData> SORTER = (a,b) ->
	{
		Object aPriorityData = a.getAnnotationData().get("priority");
		Object bPriorityData = b.getAnnotationData().get("priority");
		int priorityA = aPriorityData instanceof Integer ? (int)aPriorityData : 0;
		int priorityB = bPriorityData instanceof Integer ? (int)bPriorityData : 0;
		if (priorityA != priorityB)
			return priorityA - priorityB;
		return a.getMemberName().compareTo(b.getMemberName());
	};
	
	/**
	 * Instantiate one instance of all classes that are
	 * A) annotated with AutoCramPlugin, and
	 * B) implementing CramPlugin
	 * and then allow the instances to register cram behaviors
	 */
	public static void loadPlugins()
	{
		Cram.LOGGER.info("Loading Cram plugins!");
		
		Type cramPluginType = Type.getType(AutoCramPlugin.class);
		CramRegistrator registrator = CrammableBlocks::getCramEntry;
		
		// get the names of all classes annotated with the plugin annotation
		ModList.get().getAllScanData().stream()
			.flatMap(modData -> modData.getAnnotations().stream())
			.filter(annotationData -> Objects.equals(annotationData.getAnnotationType(), cramPluginType))
			.sorted(SORTER)
			.map(AnnotationData::getMemberName)
			
			// try to create instances of these classes
			.flatMap(Plugins::createPluginInstance)
			// and allow them to register cram behaviors if they were instantiated successfully
			.forEach(plugin -> plugin.register(registrator));
	}
	
	/**
	 * Attempts to create a plugin instance, given the name of the class to instantiate.
	 * We use a Stream instead of Optional so the mod scan stream can flatmap it.
	 * @param className The fully-qualified class name of the plugin implementation class
	 * @return A Stream containing the instance if successful, or an empty stream otherwise.
	 */
	private static Stream<CramPlugin> createPluginInstance(String className)
	{
		try
		{
			return Stream.of(
				Class.forName(className) // get the exact class by name
				.asSubclass(CramPlugin.class) // as a subclass of CramPlugin
				.newInstance()); // and try to instantiate it via its argless constructor
		}
		catch (Exception e)
		{
			Cram.LOGGER.error("Failed to load Cram Plugin: {}", className, e);
			return Stream.empty();
		}
	}
}
