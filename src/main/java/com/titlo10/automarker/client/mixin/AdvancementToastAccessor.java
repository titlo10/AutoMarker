package com.titlo10.automarker.client.mixin;

//#if MC>=260100
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.advancements.AdvancementHolder;
//#else
//$$ import net.minecraft.client.toast.AdvancementToast;
//$$ import net.minecraft.advancement.AdvancementEntry;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementToast.class)
public interface AdvancementToastAccessor {
    @Accessor("advancement")
    //#if MC>=260100
    AdvancementHolder getAdvancement();
    //#else
    //$$ AdvancementEntry getAdvancement();
    //#endif
}
