package me.guivnf.mods.hats.mixin;

import me.guivnf.mods.hats.client.render.LayerHat;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Inject(method = "extractLevel", at = @At("HEAD"))
    private void hats$beginLevel(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        LayerHat.RENDERING_LEVEL.set(true);
    }

    @Inject(method = "extractLevel", at = @At("RETURN"))
    private void hats$endLevel(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
        LayerHat.RENDERING_LEVEL.set(false);
    }
}
