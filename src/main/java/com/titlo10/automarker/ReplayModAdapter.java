package com.titlo10.automarker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Keeps ReplayMod's unstable internal recording API out of the rest of the mod. */
final class ReplayModAdapter {
    private static boolean warnedUnavailable;
    private static long lastStatusCheck;
    private static boolean cachedRecordingActive;

    private ReplayModAdapter() {
    }

    static boolean addMarker(String name) {
        try {
            Class<?> recordingClass = Class.forName("com.replaymod.recording.ReplayModRecording");
            Field instanceField = recordingClass.getField("instance");
            Object recording = instanceField.get(null);
            if (recording == null) {
                return false;
            }
            Method getHandler = recordingClass.getMethod("getConnectionEventHandler");
            Object handler = getHandler.invoke(recording);
            if (handler == null) {
                return false;
            }
            Method getListener = handler.getClass().getMethod("getPacketListener");
            Object listener = getListener.invoke(handler);
            if (listener == null) {
                return false;
            }
            Method addMarker = listener.getClass().getMethod("addMarker", String.class);
            addMarker.invoke(listener, name);
            return true;
        } catch (ReflectiveOperationException | LinkageError e) {
            if (!warnedUnavailable) {
                warnedUnavailable = true;
                AutoMarker.LOGGER.warn("ReplayMod marker API is unavailable or incompatible", e);
            }
            return false;
        }
    }

    static boolean isRecordingActive() {
        long now = System.currentTimeMillis();
        if (now - lastStatusCheck < 1000) return cachedRecordingActive;
        lastStatusCheck = now;
        try {
            Class<?> recordingClass = Class.forName("com.replaymod.recording.ReplayModRecording");
            Object recording = recordingClass.getField("instance").get(null);
            if (recording == null) return cachedRecordingActive = false;
            Object handler = recordingClass.getMethod("getConnectionEventHandler").invoke(recording);
            if (handler == null) return cachedRecordingActive = false;
            return cachedRecordingActive = handler.getClass().getMethod("getPacketListener").invoke(handler) != null;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return cachedRecordingActive = false;
        }
    }

    static void resetSession() {
        warnedUnavailable = false;
        lastStatusCheck = 0;
        cachedRecordingActive = false;
    }
}
