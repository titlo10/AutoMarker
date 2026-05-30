package com.titlo10.automarker.client.mixin;

import com.titlo10.automarker.AutoMarkerMod;
//#if MC>=260100
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//#else
//$$ import net.minecraft.client.network.ClientPlayerInteractionManager;
//$$ import net.minecraft.entity.Entity;
//$$ import net.minecraft.entity.player.PlayerEntity;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=260100
@Mixin(MultiPlayerGameMode.class)
//#else
//$$ @Mixin(ClientPlayerInteractionManager.class)
//#endif
public class MixinMultiPlayerGameMode {
    //#if MC>=260100
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Player player, Entity entity, CallbackInfo ci) {
        if (entity instanceof Player && AutoMarkerMod.config != null && AutoMarkerMod.config.enablePvpKills) {
            AutoMarkerMod.registerAttack(entity.getId(), entity.getName().getString());
        }
    }
    //#else
    //$$ @Inject(method = "attackEntity", at = @At("HEAD"))
    //$$ private void onAttack(PlayerEntity player, Entity entity, CallbackInfo ci) {
    //$$     if (entity instanceof PlayerEntity && AutoMarkerMod.config != null && AutoMarkerMod.config.enablePvpKills) {
    //$$         AutoMarkerMod.registerAttack(entity.getId(), entity.getName().getString());
    //$$     }
    //$$ }
    //#endif
}
