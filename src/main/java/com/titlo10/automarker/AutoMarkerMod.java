package com.titlo10.automarker;

import com.replaymod.recording.ReplayModRecording;
import com.replaymod.recording.packet.PacketListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

//#if MC>=260100
import net.minecraft.client.Minecraft;
//#else
//$$ import net.minecraft.client.MinecraftClient;
//#endif

public class AutoMarkerMod {
    public static AutoMarkerConfig config;

    private static final List<String> pendingAdvancements = new ArrayList<>();
    private static boolean isScheduled = false;

    private static long lastDeathTime = 0;

    private static final long CHAT_DEBOUNCE_MS = 3000;
    private static final Map<String, Long> lastChatMarkerTimes = new ConcurrentHashMap<>();

    public static void onPlayerDied() {
        long now = System.currentTimeMillis();
        if ((now - lastDeathTime) < 2000) {
            return;
        }
        lastDeathTime = now;
        addMarker(getTranslation("marker.automarker.player_died"));
    }

    public static void onChatKeyword(String keyword) {
        long now = System.currentTimeMillis();
        Long last = lastChatMarkerTimes.get(keyword);
        if (last != null && (now - last) < CHAT_DEBOUNCE_MS) {
            return;
        }
        lastChatMarkerTimes.put(keyword, now);
        addMarker(getTranslation("marker.automarker.chat", keyword));
    }



    public static void initialize() {
        config = AutoMarkerConfig.load();
    }

    public static void addMarker(String name) {
        try {
            if (ReplayModRecording.instance != null && ReplayModRecording.instance.getConnectionEventHandler() != null) {
                PacketListener packetListener = ReplayModRecording.instance.getConnectionEventHandler().getPacketListener();
                if (packetListener != null) {
                    packetListener.addMarker(name);
                    AutoMarker.LOGGER.info("Added automatic marker: {}", name);
                }
            }
        } catch (Throwable t) {
            AutoMarker.LOGGER.error("Failed to add auto-marker: " + name, t);
        }
    }

    public static void addAdvancementMarker(String title) {
        synchronized (pendingAdvancements) {
            pendingAdvancements.add(title);
            if (!isScheduled) {
                isScheduled = true;
                CompletableFuture.runAsync(() -> {
                    //#if MC>=260100
                    Minecraft.getInstance().execute(() -> {
                    //#else
                    //$$ MinecraftClient.getInstance().execute(() -> {
                    //#endif
                        List<String> titles;
                        synchronized (pendingAdvancements) {
                            titles = new ArrayList<>(pendingAdvancements);
                            pendingAdvancements.clear();
                            isScheduled = false;
                        }
                        if (!titles.isEmpty()) {
                            String combined = getTranslation("marker.automarker.advancement", String.join(", ", titles));
                            addMarker(combined);
                        }
                    });
                }, CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS));
            }
        }
    }

    public static String getTranslation(String key, Object... args) {
        try {
            String format = net.minecraft.locale.Language.getInstance().getOrDefault(key);
            if (args.length > 0) {
                return String.format(format, args);
            }
            return format;
        } catch (Throwable t) {
            if (key.equals("marker.automarker.player_died")) return "Player Died";
            if (key.equals("marker.automarker.dimension_change")) return "Dimension: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.advancement")) return "Advancement: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.chat")) return "Chat: " + (args.length > 0 ? args[0] : "");
            return key;
        }
    }
}

