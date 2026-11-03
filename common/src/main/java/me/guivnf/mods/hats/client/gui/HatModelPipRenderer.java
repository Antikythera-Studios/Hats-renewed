package me.guivnf.mods.hats.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.guivnf.mods.hats.client.render.HatRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class HatModelPipRenderer extends PictureInPictureRenderer<HatModelRenderState>
{
    public HatModelPipRenderer(MultiBufferSource.BufferSource bufferSource)
    {
        super(bufferSource);
    }

    @Override
    public Class<HatModelRenderState> getRenderStateClass()
    {
        return HatModelRenderState.class;
    }

    @Override
    protected void renderToTexture(HatModelRenderState state, PoseStack poseStack)
    {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        poseStack.scale(1f, 1f, -1f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yaw()));

        HatRenderer.setForceNoCull(true);
        HatRenderer.render(poseStack, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY,
            state.hat(), 0, 0, 0, 0, 0, 0, 1f);
        HatRenderer.setForceNoCull(false);
    }

    @Override
    protected float getTranslateY(int height, int guiScale)
    {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel()
    {
        return "hats_hat_model";
    }
}
