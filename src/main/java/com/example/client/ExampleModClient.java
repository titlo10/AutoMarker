package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

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

public class ExampleModClient implements ClientModInitializer {
    //#if MC>=260100
    private static KeyMapping configKeyBinding;
    //#else
    //$$ private static KeyBinding configKeyBinding;
    //#endif

    //#if MC>=260100
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("modid", "keys")
    );
    //#endif

    //#if MC>=12110 && MC<260100
    //$$ public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
    //$$     Identifier.of("modid", "keys")
    //$$ );
    //#endif

    @Override
    public void onInitializeClient() {
        //#if MC>=260100
        configKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.modid.config", 
            GLFW.GLFW_KEY_K, 
            CATEGORY
        ));
        //#endif

        //#if MC>=12110 && MC<260100
        //$$ configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        //$$     "key.modid.config", 
        //$$     GLFW.GLFW_KEY_K, 
        //$$     CATEGORY
        //$$ ));
        //#endif

        //#if MC<12110
        //$$ configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        //$$     "key.modid.config", 
        //$$     GLFW.GLFW_KEY_K, 
        //$$     "key.categories.modid"
        //$$ ));
        //#endif

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeyBinding.consumeClick()) {
                //#if MC>=260100
                client.setScreen(new AutoMarkerConfigScreen(client.screen));
                //#else
                //$$ client.setScreen(new AutoMarkerConfigScreen(client.currentScreen));
                //#endif
            }
        });
    }
}