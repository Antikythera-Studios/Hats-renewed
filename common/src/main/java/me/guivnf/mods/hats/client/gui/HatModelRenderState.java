package me.guivnf.mods.hats.client.gui;

import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.mixin.accessor.GuiGraphicsExtractorAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record HatModelRenderState(
    HatPart hat,
    float yaw,
    Matrix3x2f pose,
    int x0,
    int y0,
    int x1,
    int y1,
    float scale,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState
{
    public static void submit(GuiGraphicsExtractor g, HatPart hat, float yaw,
            int x0, int y0, int x1, int y1, float scale)
    {
        HatModelRenderState state = create(hat, yaw, new Matrix3x2f(g.pose()), x0, y0, x1, y1, scale, null);
        ((GuiGraphicsExtractorAccessor) g).hats$getGuiRenderState().addPicturesInPictureState(state);
    }

    public static HatModelRenderState create(HatPart hat, float yaw, Matrix3x2f pose,
            int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle area = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        ScreenRectangle bounds = scissorArea != null ? scissorArea.intersection(area) : area;
        return new HatModelRenderState(hat, yaw, pose, x0, y0, x1, y1, scale, scissorArea, bounds);
    }
}
