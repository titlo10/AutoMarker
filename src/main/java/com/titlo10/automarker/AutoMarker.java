package com.titlo10.automarker;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMarker implements ModInitializer {
	public static final String MOD_ID = "automarker";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Auto-Marker Mod...");
		AutoMarkerMod.initialize();
	}
}
