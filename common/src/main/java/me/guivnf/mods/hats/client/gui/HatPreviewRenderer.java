package me.guivnf.mods.hats.client.gui;

import me.guivnf.mods.hats.client.cache.ClientHatCache;
import me.guivnf.mods.hats.client.compat.EpicFightCompat;
import me.guivnf.mods.hats.client.render.LayerHat;
import me.guivnf.mods.hats.common.hat.HatPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class HatPreviewRenderer
{
    private HatPreviewRenderer() {}

    public static void render(GuiGraphicsExtractor g, Player player, HatPart hat,
                              int centerX, int footY, int scale,
                              int scissorX1, int scissorY1, int scissorX2, int scissorY2)
    {
        HatPart prev = ClientHatCache.getEntityHat(player.getUUID());

        HatPart show = hat.copy();
        show.isShowing = true;
        ClientHatCache.setEntityHat(player.getUUID(), show);

        EntityRenderState state;
        LayerHat.RENDERING_HAT_GUI_PREVIEW.set(true);
        EpicFightCompat.GUI_PREVIEW_ACTIVE = true;
        try {
            state = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player).createRenderState(player, 1.0F);
        } finally {
            EpicFightCompat.GUI_PREVIEW_ACTIVE = false;
            LayerHat.RENDERING_HAT_GUI_PREVIEW.set(false);
            ClientHatCache.setEntityHat(player.getUUID(), prev);
        }

        state.shadowPieces.clear();
        state.outlineColor = 0;
        state.nameTag = null;
        state.scoreText = null;
        if (state instanceof LivingEntityRenderState living) {
            living.bodyRot = 180f;
            living.yRot = 0f;
            living.xRot = -8f;
            living.walkAnimationPos = 0f;
            living.walkAnimationSpeed = 0f;
            living.boundingBoxWidth = living.boundingBoxWidth / living.scale;
            living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
            living.scale = 1.0F;
        }

        Vector3f origin = new Vector3f(0f, 0f, 0f);
        float rectCenterX = (scissorX1 + scissorX2) / 2f;
        float rectCenterY = (scissorY1 + scissorY2) / 2f;
        origin.x = (centerX - rectCenterX) / scale;
        origin.y = (footY - rectCenterY) / scale;

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateY((float) Math.toRadians(-25.0));
        g.entity(state, scale, origin, rotation, camera, scissorX1, scissorY1, scissorX2, scissorY2);
    }
}
