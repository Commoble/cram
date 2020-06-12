package com.github.commoble.cram;

import java.util.Objects;
import java.util.stream.Stream;

import org.objectweb.asm.Type;

import com.github.commoble.cram.api.AutoCramPlugin;
import com.github.commoble.cram.api.CramPlugin;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;

public class Plugins
{	
	public static void loadPlugins()
	{
		Cram.LOGGER.info("Loading Cram plugins!");
		
		Type cramPluginType = Type.getType(AutoCramPlugin.class);
		
		// get the names of all classes annotated with the plugin annotation
		ModList.get().getAllScanData().stream()
			.flatMap(modData -> modData.getAnnotations().stream())
			.filter(annotationData -> Objects.equals(annotationData.getAnnotationType(), cramPluginType)) 
			.map(AnnotationData::getMemberName)
			
			// try to create instances of these classes
			.flatMap(Plugins::createPluginInstance)
			.forEach(plugin -> plugin.register());
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
