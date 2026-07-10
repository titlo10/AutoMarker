package com.titlo10.automarker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.lwjgl.glfw.GLFW;
import com.titlo10.automarker.AutoMarkerMod;

//#if MC>=260100
import net.minecraft.client.KeyMapping;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.resources.Identifier;
//#endif

//#if MC>=12110 && MC<260100
//$$ import net.minecraft.client.option.KeyBinding;
//$$ import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//$$ import net.minecraft.util.Identifier;
//#endif

//#if MC<12110
//$$ import net.minecraft.client.option.KeyBinding;
//$$ import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//#endif

public class AutoMarkerClient implements ClientModInitializer {
    //#if MC>=260100
    private static KeyMapping configKeyBinding;
    //#else
    //$$ private static KeyBinding configKeyBinding;
    //#endif

    private static String lastDimension = null;

    //#if MC>=260100
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("automarker", "keys")
    );
    //#endif

    //#if MC>=12110 && MC<260100
    //$$ public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
    //$$     Identifier.of("automarker", "keys")
    //$$ );
    //#endif

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            lastDimension = null;
            AutoMarkerMod.resetSession();
        });

        //#if MC>=260100
        configKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.automarker.config", 
            GLFW.GLFW_KEY_K, 
            CATEGORY
        ));
        //#endif

        //#if MC>=12110 && MC<260100
        //$$ configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        //$$     "key.automarker.config", 
        //$$     GLFW.GLFW_KEY_K, 
        //$$     CATEGORY
        //$$ ));
        //#endif

        //#if MC<12110
        //$$ configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        //$$     "key.automarker.config", 
        //$$     GLFW.GLFW_KEY_K, 
        //$$     "key.categories.automarker"
        //$$ ));
        //#endif

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AutoMarkerMod.clientTick();
            while (configKeyBinding.consumeClick()) {
                //#if MC>=260100
                client.setScreen(new AutoMarkerConfigScreen(client.screen));
                //#else
                //$$ client.setScreen(new AutoMarkerConfigScreen(client.currentScreen));
                //#endif
            }

            if (AutoMarkerMod.config != null && AutoMarkerMod.config.enableDimensionChanges) {
                //#if MC>=260100
                if (client.player != null && client.level != null) {
                    String currentDim = client.level.dimension().identifier().toString();
                    if (lastDimension == null) {
                        lastDimension = currentDim;
                    } else if (!lastDimension.equals(currentDim)) {
                        lastDimension = currentDim;
                        String cleanName = AutoMarkerMod.getDimensionName(currentDim);
                        AutoMarkerMod.addMarker(AutoMarkerMod.getTranslation("marker.automarker.dimension_change", cleanName));
                    }
                }
                //#else
                //$$ if (client.player != null && client.world != null) {
                //$$     String currentDim = client.world.getRegistryKey().getValue().toString();
                //$$     if (lastDimension == null) {
                //$$         lastDimension = currentDim;
                //$$     } else if (!lastDimension.equals(currentDim)) {
                //$$         lastDimension = currentDim;
                //$$         String cleanName = AutoMarkerMod.getDimensionName(currentDim);
                //$$         AutoMarkerMod.addMarker(AutoMarkerMod.getTranslation("marker.automarker.dimension_change", cleanName));
                //$$     }
                //$$ }
                //#endif
            }
        });
    }
}
