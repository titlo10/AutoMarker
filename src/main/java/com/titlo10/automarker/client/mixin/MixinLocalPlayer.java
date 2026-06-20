package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.player.LocalPlayer;
//#else
//$$ import net.minecraft.client.network.ClientPlayerEntity;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=260100
@Mixin(LocalPlayer.class)
//#else
//$$ @Mixin(ClientPlayerEntity.class)
//#endif
public class MixinLocalPlayer {
    //#if MC>=260100
    @Inject(method = "tickDeath()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    //#else
    //$$ @Inject(method = "updatePostDeath()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;remove(Lnet/minecraft/entity/Entity$RemovalReason;)V"))
    //#endif
    protected void onTickDeath(CallbackInfo ci) {
        if (AutoMarkerMod.config != null && AutoMarkerMod.config.enableDeaths) {
            AutoMarkerMod.onPlayerDied();
        }
    }
}
