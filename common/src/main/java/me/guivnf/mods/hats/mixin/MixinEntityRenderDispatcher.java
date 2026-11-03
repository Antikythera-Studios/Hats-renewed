package me.guivnf.mods.hats.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.guivnf.mods.hats.client.compat.EpicFightCompat;
import me.guivnf.mods.hats.client.compat.GeckoLibCompat;
import me.guivnf.mods.hats.client.render.HatRenderStateAccess;
import me.guivnf.mods.hats.client.render.HatRenderer;
import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.placement.HatPlacementInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher
{
    private static final float EF_HEAD_CROWN_OFFSET = 7.75f / 16.0f;

    private static final float Z_FIGHT_INFLATE  = 1.012f;
    private static final float HEAD_HALF_OFFSET = 4.0f / 16.0f;

    @Inject(method = "submit",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
                     shift = At.Shift.AFTER))
    private void hats$fallbackRender(EntityRenderState state, CameraRenderState camera, double camX, double camY, double camZ,
            PoseStack poseStack, SubmitNodeCollector collector, CallbackInfo ci)
    {
        HatRenderStateAccess access = (HatRenderStateAccess) state;
        if (access.hats$isLayerRendered() || access.hats$isHatHidden()) return;
        if (!(state instanceof LivingEntityRenderState livingState)) return;

        HatPart hat = access.hats$getHat();
        LivingEntity living = access.hats$getEntity();
        if (hat == null || living == null) return;

        float partialTick = net.minecraft.client.Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        HatPlacementInfo placement = access.hats$getPlacement();
        if (placement == null) return;

        float bodyYaw   = livingState.bodyRot;
        float headYaw   = livingState.bodyRot + livingState.yRot;
        float headPitch = livingState.xRot;

        poseStack.pushPose();

        boolean isGecko      = GeckoLibCompat.PRESENT  && GeckoLibCompat.isGeckoLibEntity(living);
        boolean inGuiPreview = access.hats$isGuiPreview();
        boolean isEpicFight  = !inGuiPreview && EpicFightCompat.PRESENT && EpicFightCompat.isEpicFightEntity(living);

        float bbScale    = Mth.clamp(state.boundingBoxWidth / 0.6f, 0.2f, 3.0f);
        float finalScale = isEpicFight ? placement.scale : placement.scale * bbScale;

        // if (firstLog) {
        //     LOGGER.info("[Hats] FALLBACK render for entity={} isGecko={} isEpicFight={} bodyYaw={} headYaw={} headPitch={}",
        //         entity.getClass().getSimpleName(), isGecko, isEpicFight, bodyYaw, headYaw, headPitch);
        // }

        if (isEpicFight) {
            org.joml.Matrix4f headMat = EpicFightCompat.getHeadTransform(living, partialTick);
            // long now = System.currentTimeMillis();
            // if (now - lastEfLogMs > 2000L) {
            //     lastEfLogMs = now;
            //     LOGGER.info("[Hats-EF] headMatNull={} bodyYaw={} headYaw={} headPitch={} bbHeight={}",
            //         headMat == null, bodyYaw, headYaw, headPitch, state.boundingBoxHeight);
            //     if (headMat != null) {
            //         LOGGER.info("[Hats-EF] headMat tx={} ty={} tz={}",
            //             headMat.m30(), headMat.m31(), headMat.m32());
            //         LOGGER.info("[Hats-EF] headMat col0=[{},{},{}] col1=[{},{},{}] col2=[{},{},{}]",
            //             headMat.m00(), headMat.m10(), headMat.m20(),
            //             headMat.m01(), headMat.m11(), headMat.m21(),
            //             headMat.m02(), headMat.m12(), headMat.m22());
            //     }
            // }
            if (headMat != null) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                if (!EpicFightCompat.applyEFModelMatrix(poseStack, living, partialTick)) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(-bodyYaw));
                }
                if (living.isCrouching()) poseStack.translate(0.0, 0.15, 0.0);
                poseStack.last().pose().mul(headMat);
                poseStack.last().normal().mul(new org.joml.Matrix3f(headMat));
                poseStack.scale(-1f, -1f, 1f);
                poseStack.translate(0.0, -EF_HEAD_CROWN_OFFSET, 0.0);
                poseStack.translate(0.0,  HEAD_HALF_OFFSET, 0.0);
                poseStack.scale(Z_FIGHT_INFLATE, Z_FIGHT_INFLATE, Z_FIGHT_INFLATE);
                poseStack.translate(0.0, -HEAD_HALF_OFFSET, 0.0);
            } else {
                poseStack.translate(0.0, state.boundingBoxHeight, 0.0);
                poseStack.scale(-1f, -1f, 1f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180f - bodyYaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
            }
        } else if (isGecko) {
            org.joml.Matrix4f headMatrix = GeckoLibCompat.getHeadMatrix(living);
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - bodyYaw));
            if (headMatrix != null) {
                poseStack.last().pose().mul(headMatrix);
            } else {
                poseStack.translate(0.0, living.getEyeHeight() + 0.1f, 0.0);
                poseStack.mulPose(Axis.YP.rotationDegrees(-(headYaw - bodyYaw)));
                poseStack.mulPose(Axis.XP.rotationDegrees(-headPitch));
            }
            poseStack.scale(-1f, -1f, 1f);
        } else {
            poseStack.translate(0.0, state.boundingBoxHeight, 0.0);
            poseStack.scale(-1f, -1f, 1f);
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - 2f * bodyYaw + headYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
        }

        HatRenderer.render(poseStack, collector, state.lightCoords,
            OverlayTexture.pack(0, 10),
            hat, placement.offsetX, placement.offsetY, placement.offsetZ,
            placement.rotX, placement.rotY, placement.rotZ, finalScale);
        poseStack.popPose();
    }
}
