package com.example;

import com.replaymod.recording.ReplayModRecording;
import com.replaymod.recording.packet.PacketListener;

public class AutoMarkerMod {
    public static AutoMarkerConfig config;

    public static void initialize() {
        config = AutoMarkerConfig.load();
    }

    public static void addMarker(String name) {
        try {
            if (ReplayModRecording.instance != null && ReplayModRecording.instance.getConnectionEventHandler() != null) {
                PacketListener packetListener = ReplayModRecording.instance.getConnectionEventHandler().getPacketListener();
                if (packetListener != null) {
                    packetListener.addMarker(name);
                    ExampleMod.LOGGER.info("Added automatic marker: {}", name);
                }
            }
        } catch (Throwable t) {
            ExampleMod.LOGGER.error("Failed to add auto-marker: " + name, t);
        }
    }
}
