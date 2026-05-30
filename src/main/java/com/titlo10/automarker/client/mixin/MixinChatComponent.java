package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
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
            if (AutoMarkerMod.config.enablePvpKills) {
                //#if MC>=260100
                if (message.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents) {
                    net.minecraft.network.chat.contents.TranslatableContents translatable = (net.minecraft.network.chat.contents.TranslatableContents) message.getContents();
                    String key = translatable.getKey();
                    if (key.startsWith("death.attack.")) {
                        Object[] args = translatable.getArgs();
                        if (args.length > 1 && args[1] instanceof net.minecraft.network.chat.Component) {
                            String killerName = ((net.minecraft.network.chat.Component) args[1]).getString();
                            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
                            if (minecraft.player != null) {
                                String localPlayerName = minecraft.player.getName().getString();
                                if (localPlayerName.equals(killerName)) {
                                    String victimName = args[0] instanceof net.minecraft.network.chat.Component
                                        ? ((net.minecraft.network.chat.Component) args[0]).getString()
                                        : args[0].toString();
                                    AutoMarkerMod.onPlayerKilled(victimName);
                                }
                            }
                        }
                    }
                }
                //#else
                //$$ if (message.getContent() instanceof net.minecraft.text.TranslatableTextContent) {
                //$$     net.minecraft.text.TranslatableTextContent translatable = (net.minecraft.text.TranslatableTextContent) message.getContent();
                //$$     String key = translatable.getKey();
                //$$     if (key.startsWith("death.attack.")) {
                //$$         Object[] args = translatable.getArgs();
                //$$         if (args.length > 1) {
                //$$             String killerName = "";
                //$$             if (args[1] instanceof net.minecraft.text.Text) {
                //$$                 killerName = ((net.minecraft.text.Text) args[1]).getString();
                //$$             } else {
                //$$                 killerName = args[1].toString();
                //$$             }
                //$$             net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
                //$$             if (client.player != null) {
                //$$                 String localPlayerName = client.player.getName().getString();
                //$$                 if (localPlayerName.equals(killerName)) {
                //$$                     String victimName = args[0] instanceof net.minecraft.text.Text
                //$$                         ? ((net.minecraft.text.Text) args[0]).getString()
                //$$                         : args[0].toString();
                //$$                     AutoMarkerMod.onPlayerKilled(victimName);
                //$$                 }
                //$$             }
                //$$         }
                //$$     }
                //$$ }
                //#endif
            }

            String text = message.getString();
            String keywordsSetting = AutoMarkerMod.config.chatKeywords;
            if (keywordsSetting != null && !keywordsSetting.isEmpty()) {
                String[] keywords = keywordsSetting.split(",");
                for (String keyword : keywords) {
                    String trimmed = keyword.trim();
                    if (!trimmed.isEmpty() && text.contains(trimmed)) {
                        AutoMarkerMod.addMarker(AutoMarkerMod.getTranslation("marker.automarker.chat", trimmed));
                        break;
                    }
                }
            }
        }
    }
}
