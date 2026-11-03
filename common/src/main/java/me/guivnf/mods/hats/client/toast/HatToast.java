package me.guivnf.mods.hats.client.toast;

import me.guivnf.mods.hats.client.gui.HatModelRenderState;
import me.guivnf.mods.hats.common.hat.HatPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;

public class HatToast
        implements Toast
{
    private static final long DISPLAY_TIME_MS = 5000L;
    private static final int W = 160;
    private static final int H = 32;

    private final HatPart hat;
    private final boolean isAccessory;
    private Visibility wantedVisibility = Visibility.SHOW;

    public HatToast(HatPart hat, boolean isAccessory)
    {
        this.hat = hat;
        this.isAccessory = isAccessory;
    }

    @Override
    public int width()  { return W; }

    @Override
    public int height() { return H; }

    @Override
    public Visibility getWantedVisibility()
    {
        return wantedVisibility;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs)
    {
        wantedVisibility = fullyVisibleForMs >= DISPLAY_TIME_MS ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs)
    {
        graphics.fill(0, 0, W, H, 0xFFFFFFFF);
        graphics.fill(0, 0, W, 1,     0xFFCCCCCC);
        graphics.fill(0, H - 1, W, H, 0xFFAAAAAA);
        graphics.fill(0, 0, 1, H,     0xFFCCCCCC);
        graphics.fill(W - 1, 0, W, H, 0xFFAAAAAA);

        String titleKey = isAccessory ? "hats.toast.new_accessory" : "hats.toast.new_hat";
        graphics.text(font, Component.translatable(titleKey), H + 4, 7, 0xFF333333, false);
        graphics.text(font, Component.literal(hat.name), H + 4, 18, 0xFF555555, false);

        HatPart renderPart = new HatPart(hat.name);
        renderPart.isShowing = true;

        float yaw = (fullyVisibleForMs / 3000f) * 360f;
        HatModelRenderState.submit(graphics, renderPart, yaw, 0, 0, H, H, 14f);
    }

    public static void show(HatPart hat, boolean isAccessory)
    {
        Minecraft.getInstance().getToastManager().addToast(new HatToast(hat, isAccessory));
    }
}
