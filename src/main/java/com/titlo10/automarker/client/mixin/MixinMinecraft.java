package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.DeathScreen;
//#else
//$$ import net.minecraft.client.MinecraftClient;
//$$ import net.minecraft.client.gui.screen.Screen;
//$$ import net.minecraft.client.gui.screen.DeathScreen;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=260100
@Mixin(Minecraft.class)
//#else
//$$ @Mixin(MinecraftClient.class)
//#endif
public class MixinMinecraft {
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof DeathScreen && AutoMarkerMod.config != null && AutoMarkerMod.config.enableDeaths) {
            AutoMarkerMod.addMarker(AutoMarkerMod.getTranslation("marker.automarker.player_died"));
        }
    }
}
