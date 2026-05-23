package com.example.client.mixin;

import com.example.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
//#else
//$$ import net.minecraft.client.toast.ToastManager;
//$$ import net.minecraft.client.toast.Toast;
//$$ import net.minecraft.client.toast.AdvancementToast;
//$$ import net.minecraft.advancement.AdvancementEntry;
//$$ import net.minecraft.text.Text;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class MixinToastManager {
    @Inject(method = "addToast", at = @At("HEAD"))
    private void onAddToast(Toast toast, CallbackInfo ci) {
        if (toast instanceof AdvancementToast && AutoMarkerMod.config != null && AutoMarkerMod.config.enableAchievements) {
            try {
                //#if MC>=260100
                AdvancementHolder entry = ((AdvancementToastAccessor) toast).getAdvancement();
                //#else
                //$$ AdvancementEntry entry = ((AdvancementToastAccessor) toast).getAdvancement();
                //#endif
                if (entry != null && entry.value() != null) {
                    entry.value().display().ifPresent(display -> {
                        //#if MC>=260100
                        Component titleComponent = display.getTitle();
                        //#else
                        //$$ Text titleComponent = display.getTitle();
                        //#endif
                        if (titleComponent != null) {
                            String title = titleComponent.getString();
                            AutoMarkerMod.addMarker("Advancement: " + title);
                        }
                    });
                }
            } catch (Throwable t) {
                // Prevent crash if accessor fails
            }
        }
    }
}
