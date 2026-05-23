package com.example.client.mixin;

import com.example.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
//#else
//$$ import net.minecraft.client.gui.hud.ChatHud;
//$$ import net.minecraft.text.Text;
//$$ import net.minecraft.network.message.MessageSignatureData;
//$$ import net.minecraft.client.gui.hud.MessageIndicator;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=260100
@Mixin(ChatComponent.class)
//#else
//$$ @Mixin(ChatHud.class)
//#endif
public class MixinChatComponent {
    //#if MC>=260100
    @Inject(
        method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
        at = @At("HEAD")
    )
    private void onAddMessage(Component message, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        handleChatMessage(message);
    }
    //#else
    //$$ @Inject(
    //$$     method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
    //$$     at = @At("HEAD")
    //$$ )
    //$$ private void onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
    //$$     handleChatMessage(message);
    //$$ }
    //#endif

    //#if MC>=260100
    private void handleChatMessage(Component message) {
    //#else
    //$$ private void handleChatMessage(Text message) {
    //#endif
        if (message != null && AutoMarkerMod.config != null) {
            String text = message.getString();
            String keywordsSetting = AutoMarkerMod.config.chatKeywords;
            if (keywordsSetting != null && !keywordsSetting.isEmpty()) {
                String[] keywords = keywordsSetting.split(",");
                for (String keyword : keywords) {
                    String trimmed = keyword.trim();
                    if (!trimmed.isEmpty() && text.contains(trimmed)) {
                        AutoMarkerMod.addMarker(AutoMarkerMod.getTranslation("marker.automarker.chat", trimmed));
                        break; // Trigger at most one marker per chat line
                    }
                }
            }
        }
    }
}
