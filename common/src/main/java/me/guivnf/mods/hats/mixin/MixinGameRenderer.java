package me.guivnf.mods.hats.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
    private void hats$resetFrameCounter(net.minecraft.client.DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        me.guivnf.mods.hats.client.render.LayerHat.resetFrameCounter();
    }
}
