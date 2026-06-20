package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
//#else
//$$ import net.minecraft.client.MinecraftClient;
//$$ import net.minecraft.client.network.ClientPlayNetworkHandler;
//$$ import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
//$$ import net.minecraft.entity.Entity;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=260100
@Mixin(ClientPacketListener.class)
//#else
//$$ @Mixin(ClientPlayNetworkHandler.class)
//#endif
public class MixinClientPacketListener {
    //#if MC>=260100
    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void onTotemEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (AutoMarkerMod.config != null && AutoMarkerMod.config.enableTotemPops && packet.getEventId() == 35) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
                Entity entity = packet.getEntity(mc.level);
                if (entity == mc.player) {
                    AutoMarkerMod.onTotemPop();
                }
            }
        }
    }
    //#else
    //$$ @Inject(method = "onEntityStatus", at = @At("HEAD"))
    //$$ private void onTotemEntityEvent(EntityStatusS2CPacket packet, CallbackInfo ci) {
    //$$     if (AutoMarkerMod.config != null && AutoMarkerMod.config.enableTotemPops && packet.getStatus() == 35) {
    //$$         MinecraftClient mc = MinecraftClient.getInstance();
    //$$         if (mc.player != null && mc.world != null) {
    //$$             Entity entity = packet.getEntity(mc.world);
    //$$             if (entity == mc.player) {
    //$$                 AutoMarkerMod.onTotemPop();
    //$$             }
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
