package me.guivnf.mods.hats.client.gui;

import me.guivnf.mods.hats.mixin.accessor.GuiGraphicsExtractorAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;

public record PlacementViewportRenderState(
    @Nullable EntityRenderState entityState,
    float camYaw,
    float camPitch,
    float renderScale,
    float originX,
    float originY,
    float bbHeight,
    boolean drawMarker,
    float markerHalfSize,
    float offsetX,
    float offsetY,
    float offsetZ,
    float rotX,
    float rotY,
    float rotZ,
    int x0,
    int y0,
    int x1,
    int y1,
    float scale,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState
{
    public void submit(GuiGraphicsExtractor g)
    {
        ((GuiGraphicsExtractorAccessor) g).hats$getGuiRenderState().addPicturesInPictureState(this);
    }

    public static ScreenRectangle boundsOf(int x0, int y0, int x1, int y1)
    {
        return new ScreenRectangle(x0, y0, x1 - x0, y1 - y0);
    }
}
