package me.guivnf.mods.hats.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.guivnf.mods.hats.common.entity.HatEntity;
import me.guivnf.mods.hats.common.hat.HatPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class HatEntityRenderer extends EntityRenderer<HatEntity, HatEntityRenderState>
{
    public HatEntityRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public HatEntityRenderState createRenderState()
    {
        return new HatEntityRenderState();
    }

    @Override
    public void extractRenderState(HatEntity entity, HatEntityRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.hat = entity.getHatPart();
        state.rotY = Mth.lerp(partialTick, entity.lastRotY, entity.rotY);
        state.rotX = Mth.clamp(Mth.lerp(partialTick, entity.lastRotX, entity.rotX) % 360f, -50f, 50f);
    }

    @Override
    public void submit(HatEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
            CameraRenderState camera)
    {
        HatPart hat = state.hat;
        if (hat != null) {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.5, 0.0);

            poseStack.mulPose(Axis.YP.rotationDegrees(state.rotY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotX));
            poseStack.mulPose(Axis.XP.rotationDegrees(180f));

            hat.isShowing = true;
            HatRenderer.render(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY,
                hat, 0, 0, 0, 0, 0, 0, 1f);

            poseStack.popPose();
        }

        super.submit(state, poseStack, collector, camera);
    }
}
