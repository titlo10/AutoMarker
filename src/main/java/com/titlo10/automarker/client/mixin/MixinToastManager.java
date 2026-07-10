package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
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
//#if MC>=12002
//$$ import net.minecraft.advancement.AdvancementEntry;
//#else
//$$ import net.minecraft.advancement.Advancement;
//#endif
//$$ import net.minecraft.text.Text;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class MixinToastManager {
    private static boolean automarker$warnedAdvancementFailure;
    @Inject(method = "addToast", at = @At("HEAD"))
    private void onAddToast(Toast toast, CallbackInfo ci) {
        if (toast instanceof AdvancementToast && AutoMarkerMod.config != null && AutoMarkerMod.config.enableAchievements) {
            try {
                //#if MC>=260100
                AdvancementHolder entry = ((AdvancementToastAccessor) toast).getAdvancement();
                if (entry != null && entry.value() != null) {
                    entry.value().display().ifPresent(display -> {
                        Component titleComponent = display.getTitle();
                        if (titleComponent != null) {
                            String title = titleComponent.getString();
                            AutoMarkerMod.addAdvancementMarker(title);
                        }
                    });
                }
                //#else
                //#if MC>=12002
                //$$ AdvancementEntry entry = ((AdvancementToastAccessor) toast).getAdvancement();
                //$$ if (entry != null && entry.value() != null) {
                //$$     entry.value().display().ifPresent(display -> {
                //$$         Text titleComponent = display.getTitle();
                //$$         if (titleComponent != null) {
                //$$             String title = titleComponent.getString();
                //$$             AutoMarkerMod.addAdvancementMarker(title);
                //$$         }
                //$$     });
                //$$ }
                //#else
                //$$ Advancement entry = ((AdvancementToastAccessor) toast).getAdvancement();
                //$$ if (entry != null) {
                //$$     net.minecraft.advancement.AdvancementDisplay display = entry.getDisplay();
                //$$     if (display != null) {
                //$$         Text titleComponent = display.getTitle();
                //$$         if (titleComponent != null) {
                //$$             String title = titleComponent.getString();
                //$$             AutoMarkerMod.addAdvancementMarker(title);
                //$$         }
                //$$     }
                //$$ }
                //#endif
                //#endif
            } catch (Throwable t) {
                if (!automarker$warnedAdvancementFailure) {
                    automarker$warnedAdvancementFailure = true;
                    com.titlo10.automarker.AutoMarker.LOGGER.warn("Failed to read an advancement toast", t);
                }
            }
        }
    }
}
