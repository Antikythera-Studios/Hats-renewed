package me.guivnf.mods.hats.mixin;

import me.guivnf.mods.hats.client.event.ClientEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Shadow
    private Entity entity;

    @Shadow
    private boolean detached;

    @Inject(method = "calculateFov", at = @At("HEAD"), cancellable = true)
    private void hats$overrideFov(float partialTick, CallbackInfoReturnable<Float> cir) {
        float progress = ClientEventHandler.hatMenuProgressPrev
                + (ClientEventHandler.hatMenuProgress - ClientEventHandler.hatMenuProgressPrev) * partialTick;

        if (progress <= 0f)
            return;

        cir.setReturnValue(100.0f);
    }

    @Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void hats$overrideForGui(float partialTick, CallbackInfo ci) {
        if (!detached || entity == null)
            return;

        float progress = ClientEventHandler.hatMenuProgressPrev
                + (ClientEventHandler.hatMenuProgress - ClientEventHandler.hatMenuProgressPrev) * partialTick;

        if (progress <= 0f)
            return;

        float guiYaw   = ClientEventHandler.guiYaw;
        float guiPitch = ClientEventHandler.guiPitch;
        float guiZoom  = ClientEventHandler.guiZoom;
        float guiPanX  = ClientEventHandler.guiPanX;
        float guiPanY  = ClientEventHandler.guiPanY;

        float playerYaw = entity.getViewYRot(partialTick);

        float camYaw   = playerYaw + 180f + guiYaw;
        float camPitch = Mth.clamp(Mth.lerp(progress, 0f, guiPitch), -85f, 85f);

        float camDist    = progress * Mth.clamp(1.8f - guiZoom, 0.2f, 1.8f);
        float lateralOff = progress * 2.0f + guiPanX;

        float yawRad   = camYaw   * (float)(Math.PI / 180.0);
        float pitchRad = camPitch * (float)(Math.PI / 180.0);
        float cosPitch = Mth.cos(pitchRad);
        float sinYaw   = Mth.sin(yawRad);
        float cosYaw   = Mth.cos(yawRad);
        float sinPitch = Mth.sin(pitchRad);

        float fwdX = -sinYaw * cosPitch;
        float fwdY = -sinPitch;
        float fwdZ =  cosYaw * cosPitch;

        double eyeX = Mth.lerp(partialTick, entity.xo, entity.getX());
        double eyeY = Mth.lerp(partialTick, entity.yo, entity.getY()) + entity.getEyeHeight();
        double eyeZ = Mth.lerp(partialTick, entity.zo, entity.getZ());

        double camX = eyeX - fwdX * camDist - cosYaw * lateralOff;
        double camY = eyeY - fwdY * camDist + guiPanY;
        double camZ = eyeZ - fwdZ * camDist - sinYaw * lateralOff;

        setRotation(camYaw, camPitch);
        setPosition(new Vec3(camX, camY, camZ));
    }
}
