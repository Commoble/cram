package com.github.commoble.cram.client;

import com.github.commoble.cram.util.ConfigHelper;
import com.github.commoble.cram.util.ConfigHelper.ConfigValueListener;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class ClientConfig
{
	public static ClientConfig INSTANCE;
	
	// called during mod object construction on client side
	public static void initClientConfig()
	{
		INSTANCE = ConfigHelper.register(ModConfig.Type.CLIENT, ClientConfig::new);
	}
	
	
	public ConfigValueListener<Boolean> showPlacementPreview;
	public ConfigValueListener<Double> previewPlacementOpacity;
	
	public ClientConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
	{
		builder.push("Rendering");
		this.showPlacementPreview = subscriber.subscribe(builder
			.comment("Render preview of crammable blocks before they are crammed into place")
			.translation("cram.showPlacementPreview")
			.define("showPlacementPreview", true));
		this.previewPlacementOpacity = subscriber.subscribe(builder
			.comment("Placement preview opacity")
			.translation("cram.previewPlacementOpacity")
			.defineInRange("previewPlacementOpacity", 0.4D, 0D, 1D));
		builder.pop();
	}
}