package com.titlo10.automarker;

import com.replaymod.recording.ReplayModRecording;
import com.replaymod.recording.packet.PacketListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // PvP kill detection (chat-driven): parse server death messages where the local
    // player is the killer. Server-independent and reliable, unlike client heuristics.
    private static final long KILL_DEBOUNCE_MS = 2000;
    private static final Map<String, Long> lastKillMarkerTimes = new ConcurrentHashMap<>();

    /**
     * Death-message pattern. The regex must expose two named groups: {@code victim} and
     * {@code killer}. Matching is done with {@link Matcher#find()}, so server prefixes
     * (e.g. "[Lobby] ") don't break detection.
     */
    private static final class KillPattern {
        final Pattern pattern;
        KillPattern(Pattern pattern) {
            this.pattern = pattern;
        }
    }

    // Username token: stops at punctuation/spaces so trailing ". (+2 assist)" etc. is excluded.
    private static final String NAME = "(?<%s>\\S+)";

    // Vanilla death-message translation keys whose format is "<victim> ... <killer> [...]"
    // (i.e. %1$s = victim, %2$s = killer). These are resolved through the client's active
    // Language, so detection automatically follows the client locale and works in every
    // language Minecraft ships — including all of our supported translations.
    private static final String[] VANILLA_DEATH_KEYS = new String[]{
        "death.attack.player",
        "death.attack.mob",
        "death.attack.arrow",
        "death.attack.trident",
        "death.attack.thrown",
        "death.attack.fireball",
        "death.attack.indirectMagic",
        "death.attack.explosion.player",
        "death.attack.sting",
        "death.attack.witherSkull",
        "death.attack.thorns",
    };

    // Extra non-vanilla phrasings used by popular minigame servers (Hypixel, CubeCraft, ...).
    // These are sent as plain text and are effectively always English regardless of client
    // locale, so they're kept alongside the locale-derived vanilla patterns.
    private static final KillPattern[] CUSTOM_KILL_PATTERNS = new KillPattern[]{
        // "<victim> was slain by <killer>"
        new KillPattern(Pattern.compile(named("victim") + "\\s+was slain by\\s+" + named("killer"))),
        // "<victim> was killed by <killer>"
        new KillPattern(Pattern.compile(named("victim") + "\\s+was killed by\\s+" + named("killer"))),
        // "<victim> was shot by <killer>"
        new KillPattern(Pattern.compile(named("victim") + "\\s+was shot by\\s+" + named("killer"))),
        // "<killer> has killed <victim>"
        new KillPattern(Pattern.compile(named("killer") + "\\s+has killed\\s+" + named("victim"))),
    };

    // Matches Java format placeholders: indexed "%1$s" (group 1 = index) or plain "%s".
    private static final Pattern FORMAT_PLACEHOLDER = Pattern.compile("%(?:(\\d+)\\$)?s");

    // Vanilla death-message translation keys (the victim is always %1$s). Used to detect the
    // LOCAL player's own death from chat — essential on servers where the death screen never
    // shows. Resolved through the client Language, so this follows the client locale and works
    // in every supported language. ".item" variants are omitted because the base key's text is a
    // prefix of theirs and matches via find(); ".player" variants are kept (different wording).
    // Unknown keys on a given version resolve to themselves and are skipped during compilation.
    private static final String[] VANILLA_DEATH_VICTIM_KEYS = new String[]{
        "death.attack.anvil", "death.attack.anvil.player",
        "death.attack.arrow",
        "death.attack.cactus", "death.attack.cactus.player",
        "death.attack.cramming", "death.attack.cramming.player",
        "death.attack.dragonBreath", "death.attack.dragonBreath.player",
        "death.attack.drown", "death.attack.drown.player",
        "death.attack.dryout", "death.attack.dryout.player",
        "death.attack.even_more_magic",
        "death.attack.explosion", "death.attack.explosion.player",
        "death.attack.fall", "death.attack.fall.player",
        "death.attack.fallingBlock", "death.attack.fallingBlock.player",
        "death.attack.fallingStalactite", "death.attack.fallingStalactite.player",
        "death.attack.fireball",
        "death.attack.fireworks", "death.attack.fireworks.player",
        "death.attack.flyIntoWall", "death.attack.flyIntoWall.player",
        "death.attack.freeze", "death.attack.freeze.player",
        "death.attack.generic", "death.attack.generic.player",
        "death.attack.genericKill", "death.attack.genericKill.player",
        "death.attack.hotFloor", "death.attack.hotFloor.player",
        "death.attack.indirectMagic",
        "death.attack.inFire", "death.attack.inFire.player",
        "death.attack.inWall", "death.attack.inWall.player",
        "death.attack.lava", "death.attack.lava.player",
        "death.attack.lightningBolt", "death.attack.lightningBolt.player",
        "death.attack.mace_smash",
        "death.attack.magic", "death.attack.magic.player",
        "death.attack.mob",
        "death.attack.onFire", "death.attack.onFire.player",
        "death.attack.outOfWorld", "death.attack.outOfWorld.player",
        "death.attack.outsideBorder", "death.attack.outsideBorder.player",
        "death.attack.player",
        "death.attack.sonic_boom",
        "death.attack.stalagmite", "death.attack.stalagmite.player",
        "death.attack.starve", "death.attack.starve.player",
        "death.attack.sting", "death.attack.sting.player",
        "death.attack.sweetBerryBush", "death.attack.sweetBerryBush.player",
        "death.attack.thorns",
        "death.attack.thrown",
        "death.attack.trident",
        "death.attack.wither", "death.attack.wither.player",
        "death.attack.witherSkull",
        "death.attack.badRespawnPoint.message",
        "death.fell.accident.generic", "death.fell.accident.ladder",
        "death.fell.accident.other_climbable", "death.fell.accident.scaffolding",
        "death.fell.accident.twisting_vines", "death.fell.accident.vines",
        "death.fell.accident.weeping_vines",
        "death.fell.assist", "death.fell.finish", "death.fell.killer",
    };

    // Non-vanilla victim phrasings used by minigame servers (always English). Mirror of the kill
    // custom patterns, but anchored on the victim so the LOCAL player's death is caught even when
    // the client locale differs from the server's English text.
    private static final KillPattern[] CUSTOM_DEATH_PATTERNS = new KillPattern[]{
        new KillPattern(Pattern.compile(named("victim") + "\\s+was slain by\\s+\\S+")),
        new KillPattern(Pattern.compile(named("victim") + "\\s+was killed by\\s+\\S+")),
        new KillPattern(Pattern.compile(named("victim") + "\\s+was shot by\\s+\\S+")),
    };

    // Cached compiled patterns and a sentinel used to rebuild them when the client language changes.
    private static volatile KillPattern[] cachedKillPatterns = null;
    private static volatile KillPattern[] cachedDeathPatterns = null;
    private static volatile String cachedLangSentinel = null;

    private static String named(String group) {
        return String.format(NAME, group);
    }

    /** Patterns that detect the LOCAL player killing another player (killer == local). */
    private static KillPattern[] getKillPatterns() {
        ensurePatterns();
        return cachedKillPatterns;
    }

    /** Patterns that detect the LOCAL player's own death (victim == local). */
    private static KillPattern[] getDeathPatterns() {
        ensurePatterns();
        return cachedDeathPatterns;
    }

    /**
     * (Re)builds both the kill and death pattern caches from the current client language whenever
     * the locale has changed since the last build. The locale is detected cheaply by resolving a
     * single death key and comparing it against the previous result, so the common case is one map
     * lookup with no recompilation.
     */
    private static void ensurePatterns() {
        String sentinel = resolveDeathFormat("death.attack.player");
        if (cachedKillPatterns != null && cachedDeathPatterns != null
                && sentinel != null && sentinel.equals(cachedLangSentinel)) {
            return;
        }
        synchronized (AutoMarkerMod.class) {
            if (cachedKillPatterns != null && cachedDeathPatterns != null
                    && sentinel != null && sentinel.equals(cachedLangSentinel)) {
                return;
            }
            List<KillPattern> kills = new ArrayList<>();
            for (String key : VANILLA_DEATH_KEYS) {
                Pattern p = buildPatternFromFormat(resolveDeathFormat(key), true);
                if (p != null) {
                    kills.add(new KillPattern(p));
                }
            }
            for (KillPattern custom : CUSTOM_KILL_PATTERNS) {
                kills.add(custom);
            }

            List<KillPattern> deaths = new ArrayList<>();
            for (String key : VANILLA_DEATH_VICTIM_KEYS) {
                Pattern p = buildPatternFromFormat(resolveDeathFormat(key), false);
                if (p != null) {
                    deaths.add(new KillPattern(p));
                }
            }
            for (KillPattern custom : CUSTOM_DEATH_PATTERNS) {
                deaths.add(custom);
            }

            cachedKillPatterns = kills.toArray(new KillPattern[0]);
            cachedDeathPatterns = deaths.toArray(new KillPattern[0]);
            cachedLangSentinel = sentinel;
        }
    }

    private static String resolveDeathFormat(String key) {
        try {
            return net.minecraft.locale.Language.getInstance().getOrDefault(key);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Converts a Minecraft death-message format (e.g. "%1$s was slain by %2$s using %3$s") into a
     * regex with named {@code victim}/{@code killer} groups. The mapping is by placeholder index
     * (%1$s = victim, %2$s = killer), so it stays correct even when a translation reorders the
     * arguments. Returns {@code null} if the format lacks both names (e.g. an unresolved key).
     */
    private static Pattern buildPatternFromFormat(String format, boolean requireKiller) {
        if (format == null || format.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Matcher m = FORMAT_PLACEHOLDER.matcher(format);
        int last = 0;
        int plainCounter = 0;
        boolean hasVictim = false;
        boolean hasKiller = false;
        while (m.find()) {
            sb.append(Pattern.quote(format.substring(last, m.start())));
            int index = (m.group(1) != null) ? Integer.parseInt(m.group(1)) : ++plainCounter;
            if (index == 1 && !hasVictim) {
                sb.append(named("victim"));
                hasVictim = true;
            } else if (index == 2 && !hasKiller) {
                sb.append(named("killer"));
                hasKiller = true;
            } else {
                // Any further argument (item used, etc.) — match loosely without capturing.
                sb.append(".*?");
            }
            last = m.end();
        }
        sb.append(Pattern.quote(format.substring(last)));
        if (!hasVictim || (requireKiller && !hasKiller)) {
            return null;
        }
        try {
            return Pattern.compile(sb.toString());
        } catch (Throwable t) {
            return null;
        }
    }

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

    /**
     * Called for every chat message when PvP tracking is enabled. Parses known death-message
     * formats; if the killer is the local player, adds a kill marker for the victim.
     */
    public static void onPvpDeathMessage(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        String localName = getLocalPlayerName();
        if (localName == null || localName.isEmpty() || !text.contains(localName)) {
            return;
        }
        for (KillPattern kp : getKillPatterns()) {
            Matcher matcher = kp.pattern.matcher(text);
            if (!matcher.find()) {
                continue;
            }
            String killer = sanitizeName(matcher.group("killer"));
            String victim = sanitizeName(matcher.group("victim"));
            if (killer.isEmpty() || victim.isEmpty()) {
                continue;
            }
            // Only our own kills, and never when we are the victim (e.g. "we died" messages).
            if (!killer.equalsIgnoreCase(localName) || victim.equalsIgnoreCase(localName)) {
                continue;
            }
            long now = System.currentTimeMillis();
            Long last = lastKillMarkerTimes.get(victim);
            if (last != null && (now - last) < KILL_DEBOUNCE_MS) {
                return;
            }
            lastKillMarkerTimes.put(victim, now);
            addMarker(getTranslation("marker.automarker.player_killed", victim));
            return;
        }
    }

    /**
     * Called for every chat message when death tracking is enabled. If a death message's victim is
     * the local player, records a death. This is the only reliable death signal on servers, where
     * the client-side death screen ({@link #onPlayerDied()} via mixin) usually never appears.
     */
    public static void onPlayerDeathMessage(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        String localName = getLocalPlayerName();
        if (localName == null || localName.isEmpty() || !text.contains(localName)) {
            return;
        }
        for (KillPattern kp : getDeathPatterns()) {
            Matcher matcher = kp.pattern.matcher(text);
            if (!matcher.find()) {
                continue;
            }
            String victim = sanitizeName(matcher.group("victim"));
            if (victim.equalsIgnoreCase(localName)) {
                onPlayerDied();
                return;
            }
        }
    }

    /** Trims leading/trailing punctuation (e.g. the trailing "." in "Steve.") from a captured name. */
    private static String sanitizeName(String name) {
        if (name == null) {
            return "";
        }
        return name.replaceAll("^[^\\p{L}\\p{N}_]+", "").replaceAll("[^\\p{L}\\p{N}_]+$", "");
    }

    private static String getLocalPlayerName() {
        try {
            //#if MC>=260100
            Minecraft mc = Minecraft.getInstance();
            //#else
            //$$ MinecraftClient mc = MinecraftClient.getInstance();
            //#endif
            if (mc != null && mc.player != null) {
                return mc.player.getName().getString();
            }
        } catch (Throwable t) {
            AutoMarker.LOGGER.error("Failed to resolve local player name", t);
        }
        return null;
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
            if (key.equals("marker.automarker.player_killed")) return "Killed: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.dimension_change")) return "Dimension: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.advancement")) return "Advancement: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.chat")) return "Chat: " + (args.length > 0 ? args[0] : "");
            return key;
        }
    }
}

