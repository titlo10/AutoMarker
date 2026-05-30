package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//#else
//$$ import net.minecraft.client.network.ClientPlayNetworkHandler;
//$$ import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
//$$ import net.minecraft.entity.Entity;
//$$ import net.minecraft.entity.player.PlayerEntity;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    @Shadow
    private net.minecraft.client.multiplayer.ClientLevel level;
    //#else
    //$$ @Shadow
    //$$ private net.minecraft.client.world.ClientWorld world;
    //#endif

    //#if MC>=260100
    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void onHandleEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (packet.getEventId() == 3) {
            Entity entity = packet.getEntity(this.level);
            if (entity instanceof Player) {
                AutoMarkerMod.checkPvPKill(entity.getId());
            }
        }
    }
    //#else
    //$$ @Inject(method = "onEntityStatus", at = @At("HEAD"))
    //$$ private void onOnEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
    //$$     if (packet.getStatus() == 3) {
    //$$         Entity entity = packet.getEntity(this.world);
    //$$         if (entity instanceof PlayerEntity) {
    //$$             AutoMarkerMod.checkPvPKill(entity.getId());
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
