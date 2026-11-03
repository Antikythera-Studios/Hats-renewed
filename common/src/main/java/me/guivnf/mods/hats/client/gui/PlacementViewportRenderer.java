package me.guivnf.mods.hats.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlacementViewportRenderer extends PictureInPictureRenderer<PlacementViewportRenderState>
{
    private static final float LINE_WIDTH = 1.0f;

    public PlacementViewportRenderer(MultiBufferSource.BufferSource bufferSource)
    {
        super(bufferSource);
    }

    @Override
    public Class<PlacementViewportRenderState> getRenderStateClass()
    {
        return PlacementViewportRenderState.class;
    }

    @Override
    protected void renderToTexture(PlacementViewportRenderState state, PoseStack poseStack)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        poseStack.translate(state.originX(), state.originY(), 0f);
        poseStack.scale(state.renderScale(), state.renderScale(), state.renderScale());

        Quaternionf pitchQuat = new Quaternionf().rotateX(state.camPitch() * Mth.DEG_TO_RAD);
        Quaternionf flipQuat  = new Quaternionf().rotateZ((float) Math.PI);
        flipQuat.mul(pitchQuat);
        flipQuat.mul(new Quaternionf().rotateY(state.camYaw() * Mth.DEG_TO_RAD));
        poseStack.mulPose(flipQuat);

        drawGrid(poseStack);

        if (state.entityState() != null) {
            FeatureRenderDispatcher features = mc.gameRenderer.getFeatureRenderDispatcher();
            CameraRenderState camera = new CameraRenderState();
            camera.orientation = new Quaternionf(pitchQuat).conjugate();
            mc.getEntityRenderDispatcher().submit(state.entityState(), camera, 0.0, 0.0, 0.0,
                poseStack, features.getSubmitNodeStorage());
            features.renderAllFeatures();
        }

        if (state.drawMarker()) drawMarker(poseStack, state);
    }

    private void drawGrid(PoseStack poseStack)
    {
        VertexConsumer buf = bufferSource.getBuffer(RenderTypes.lines());
        PoseStack.Pose pose = poseStack.last();

        int   lines   = 10;
        float spacing = 0.5f;
        float extent  = lines * spacing;

        for (int i = -lines; i <= lines; i++) {
            float p      = i * spacing;
            float bright = i == 0 ? 0.55f : 0.28f;
            float alpha  = i == 0 ? 1.0f  : 0.7f;
            int colour = argb(alpha, bright, bright, bright);

            line(buf, pose, -extent, 0f, p, extent, 0f, p, colour);
            line(buf, pose, p, 0f, -extent, p, 0f, extent, colour);
        }

        bufferSource.endBatch();
    }

    private void drawMarker(PoseStack poseStack, PlacementViewportRenderState state)
    {
        float hs = state.markerHalfSize();
        int colour = argb(1f, 1f, 0.2f, 0.2f);

        poseStack.pushPose();
        poseStack.translate(state.offsetX() / 16f, state.bbHeight() + state.offsetY() / 16f, state.offsetZ() / 16f);
        if (state.rotZ() != 0f) poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotZ()));
        if (state.rotY() != 0f) poseStack.mulPose(Axis.YP.rotationDegrees(state.rotY()));
        if (state.rotX() != 0f) poseStack.mulPose(Axis.XP.rotationDegrees(state.rotX()));

        VertexConsumer buf = bufferSource.getBuffer(RenderTypes.lines());
        PoseStack.Pose pose = poseStack.last();

        line(buf, pose, -hs, -hs, -hs,  hs, -hs, -hs, colour);
        line(buf, pose,  hs, -hs, -hs,  hs, -hs,  hs, colour);
        line(buf, pose,  hs, -hs,  hs, -hs, -hs,  hs, colour);
        line(buf, pose, -hs, -hs,  hs, -hs, -hs, -hs, colour);

        line(buf, pose, -hs,  hs, -hs,  hs,  hs, -hs, colour);
        line(buf, pose,  hs,  hs, -hs,  hs,  hs,  hs, colour);
        line(buf, pose,  hs,  hs,  hs, -hs,  hs,  hs, colour);
        line(buf, pose, -hs,  hs,  hs, -hs,  hs, -hs, colour);

        line(buf, pose, -hs, -hs, -hs, -hs,  hs, -hs, colour);
        line(buf, pose,  hs, -hs, -hs,  hs,  hs, -hs, colour);
        line(buf, pose,  hs, -hs,  hs,  hs,  hs,  hs, colour);
        line(buf, pose, -hs, -hs,  hs, -hs,  hs,  hs, colour);

        bufferSource.endBatch();
        poseStack.popPose();
    }

    private static void line(VertexConsumer buf, PoseStack.Pose pose,
            float x0, float y0, float z0, float x1, float y1, float z1, int colour)
    {
        Vector3f normal = new Vector3f(x1 - x0, y1 - y0, z1 - z0).normalize();
        buf.addVertex(pose, x0, y0, z0).setColor(colour).setNormal(pose, normal).setLineWidth(LINE_WIDTH);
        buf.addVertex(pose, x1, y1, z1).setColor(colour).setNormal(pose, normal).setLineWidth(LINE_WIDTH);
    }

    private static int argb(float a, float r, float g, float b)
    {
        return ((int) (a * 255f) << 24) | ((int) (r * 255f) << 16) | ((int) (g * 255f) << 8) | (int) (b * 255f);
    }

    @Override
    protected float getTranslateY(int height, int guiScale)
    {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel()
    {
        return "hats_placement_viewport";
    }
}
