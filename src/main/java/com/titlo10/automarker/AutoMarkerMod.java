package com.titlo10.automarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private static int advancementFlushTicks = -1;

    private static long lastDeathTime = 0;
    private static long lastTotemTime = 0;

    private static final long CHAT_DEBOUNCE_MS = 3000;
    private static final Map<String, Long> lastChatMarkerTimes = new ConcurrentHashMap<>();

    private static final long KILL_DEBOUNCE_MS = 2000;
    private static final Map<String, Long> lastKillMarkerTimes = new ConcurrentHashMap<>();

    private static final class KillPattern {
        final Pattern pattern;
        KillPattern(Pattern pattern) {
            this.pattern = pattern;
        }
    }

    private static final String NAME = "(?<%s>\\S+)";

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

    private static final KillPattern[] CUSTOM_KILL_PATTERNS = new KillPattern[]{
        new KillPattern(Pattern.compile(named("victim") + "\\s+was slain by\\s+" + named("killer"))),
        new KillPattern(Pattern.compile(named("victim") + "\\s+was killed by\\s+" + named("killer"))),
        new KillPattern(Pattern.compile(named("victim") + "\\s+was shot by\\s+" + named("killer"))),
        new KillPattern(Pattern.compile(named("killer") + "\\s+has killed\\s+" + named("victim"))),
    };

    private static final Pattern FORMAT_PLACEHOLDER = Pattern.compile("%(?:(\\d+)\\$)?s");

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

    private static final KillPattern[] CUSTOM_DEATH_PATTERNS = new KillPattern[]{
        new KillPattern(Pattern.compile(named("victim") + "\\s+was slain by\\s+\\S+")),
        new KillPattern(Pattern.compile(named("victim") + "\\s+was killed by\\s+\\S+")),
        new KillPattern(Pattern.compile(named("victim") + "\\s+was shot by\\s+\\S+")),
    };

    private static volatile KillPattern[] cachedKillPatterns = null;
    private static volatile KillPattern[] cachedDeathPatterns = null;
    private static volatile String cachedLangSentinel = null;

    private static String named(String group) {
        return String.format(NAME, group);
    }

    private static KillPattern[] getKillPatterns() {
        ensurePatterns();
        return cachedKillPatterns;
    }

    private static KillPattern[] getDeathPatterns() {
        ensurePatterns();
        return cachedDeathPatterns;
    }

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

    public static void onTotemPop() {
        long now = System.currentTimeMillis();
        if ((now - lastTotemTime) < 2000) {
            return;
        }
        lastTotemTime = now;
        addMarker(getTranslation("marker.automarker.totem_pop"));
    }

    public static void onChatKeyword(String keyword) {
        long now = System.currentTimeMillis();
        Long last = lastChatMarkerTimes.get(keyword);
        if (last != null && (now - last) < CHAT_DEBOUNCE_MS) {
            return;
        }
        lastChatMarkerTimes.put(keyword, now);
        trimDebounceMap(lastChatMarkerTimes);
        addMarker(getTranslation("marker.automarker.chat", keyword));
    }

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
            if (!killer.equalsIgnoreCase(localName) || victim.equalsIgnoreCase(localName)) {
                continue;
            }
            long now = System.currentTimeMillis();
            Long last = lastKillMarkerTimes.get(victim);
            if (last != null && (now - last) < KILL_DEBOUNCE_MS) {
                return;
            }
            lastKillMarkerTimes.put(victim, now);
            trimDebounceMap(lastKillMarkerTimes);
            addMarker(getTranslation("marker.automarker.player_killed", victim));
            return;
        }
    }

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

    public static boolean addMarker(String name) {
        if (ReplayModAdapter.addMarker(name)) {
            AutoMarker.LOGGER.info("Added automatic marker: {}", name);
            return true;
        }
        return false;
    }

    public static boolean isRecordingActive() {
        return ReplayModAdapter.isRecordingActive();
    }

    public static void addAdvancementMarker(String title) {
        synchronized (pendingAdvancements) {
            pendingAdvancements.add(title);
            advancementFlushTicks = 3;
        }
    }

    public static void clientTick() {
        List<String> titles = null;
        synchronized (pendingAdvancements) {
            if (advancementFlushTicks > 0) {
                advancementFlushTicks--;
            }
            if (advancementFlushTicks == 0) {
                titles = new ArrayList<>(pendingAdvancements);
                pendingAdvancements.clear();
                advancementFlushTicks = -1;
            }
        }
        if (titles != null && !titles.isEmpty()) {
            addMarker(getTranslation("marker.automarker.advancement", String.join(", ", titles)));
        }
    }

    public static void resetSession() {
        synchronized (pendingAdvancements) {
            pendingAdvancements.clear();
            advancementFlushTicks = -1;
        }
        lastDeathTime = 0;
        lastTotemTime = 0;
        lastChatMarkerTimes.clear();
        lastKillMarkerTimes.clear();
        cachedKillPatterns = null;
        cachedDeathPatterns = null;
        cachedLangSentinel = null;
        ReplayModAdapter.resetSession();
    }

    public static boolean onTranslatedDeathMessage(String key, Object[] arguments) {
        if (key == null || !key.startsWith("death.") || arguments == null || arguments.length == 0) {
            return false;
        }
        String localName = getLocalPlayerName();
        String victim = componentString(arguments[0]);
        if (localName == null || !sanitizeName(victim).equalsIgnoreCase(localName)) {
            if (config != null && config.enablePvpKills && arguments.length > 1) {
                String killer = sanitizeName(componentString(arguments[1]));
                if (killer.equalsIgnoreCase(localName) && isPlayerKillKey(key)) {
                    String cleanVictim = sanitizeName(victim);
                    if (!cleanVictim.isEmpty()) {
                        markKill(cleanVictim);
                    }
                }
            }
            return true;
        }
        if (config != null && config.enableDeaths) {
            onPlayerDied();
        }
        return true;
    }

    private static boolean isPlayerKillKey(String key) {
        for (String candidate : VANILLA_DEATH_KEYS) {
            if (candidate.equals(key) || key.startsWith(candidate + ".")) {
                return true;
            }
        }
        return false;
    }

    private static void markKill(String victim) {
        long now = System.currentTimeMillis();
        Long last = lastKillMarkerTimes.get(victim);
        if (last == null || now - last >= KILL_DEBOUNCE_MS) {
            lastKillMarkerTimes.put(victim, now);
            trimDebounceMap(lastKillMarkerTimes);
            addMarker(getTranslation("marker.automarker.player_killed", victim));
        }
    }

    private static String componentString(Object value) {
        if (value == null) return "";
        try {
            return (String) value.getClass().getMethod("getString").invoke(value);
        } catch (ReflectiveOperationException ignored) {
            return String.valueOf(value);
        }
    }

    private static void trimDebounceMap(Map<String, Long> map) {
        if (map.size() <= 256) return;
        long cutoff = System.currentTimeMillis() - Math.max(CHAT_DEBOUNCE_MS, KILL_DEBOUNCE_MS);
        map.entrySet().removeIf(entry -> entry.getValue() < cutoff);
        if (map.size() > 256) map.clear();
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
            if (key.equals("marker.automarker.totem_pop")) return "Totem Popped";
            if (key.equals("marker.automarker.dimension_change")) return "Dimension: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.advancement")) return "Advancement: " + (args.length > 0 ? args[0] : "");
            if (key.equals("marker.automarker.chat")) return "Chat: " + (args.length > 0 ? args[0] : "");
            return key;
        }
    }

    public static String getDimensionName(String id) {
        if (id == null || id.isEmpty()) return "";
        int separator = id.indexOf(':');
        String namespace = separator >= 0 ? id.substring(0, separator) : "minecraft";
        String path = separator >= 0 ? id.substring(separator + 1) : id;
        String key = "dimension." + namespace + "." + path;
        String translated = getTranslation(key);
        if (!key.equals(translated)) {
            return translated;
        }
        String readable = path.replace('_', ' ');
        return readable.isEmpty() ? id : Character.toUpperCase(readable.charAt(0)) + readable.substring(1);
    }
}
