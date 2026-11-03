package me.guivnf.mods.hats.mixin;

import me.guivnf.mods.hats.client.gui.HatModelRenderState;
import me.guivnf.mods.hats.common.hat.HatDefinition;
import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.HatRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class MixinGuiGraphics
{
    private static final String HAT_INSERTION_PREFIX = "hats:model:";
    private static final int MODEL_BOX = 44;
    private static final int MODEL_TEXT_GAP = 6;
    private static final int INNER_PAD = 4;

    @Inject(method = "componentHoverEffect", at = @At("HEAD"), cancellable = true)
    private void hats$renderHatHover(Font font, Style style, int mouseX, int mouseY, CallbackInfo ci)
    {
        if (style == null) return;
        String insertion = style.getInsertion();
        if (insertion == null || !insertion.startsWith(HAT_INSERTION_PREFIX)) return;
        if (!(style.getHoverEvent() instanceof HoverEvent.ShowText(Component tooltipText))) return;

        String payload = insertion.substring(HAT_INSERTION_PREFIX.length());
        int pipe = payload.indexOf('|');
        String regKey = pipe < 0 ? payload : payload.substring(0, pipe);
        String accCsv = pipe < 0 ? "" : payload.substring(pipe + 1);
        HatDefinition def = HatRegistry.get(regKey);
        if (def == null) return;

        java.util.Set<String> equippedAccs = new java.util.HashSet<>();
        if (!accCsv.isEmpty()) {
            for (String a : accCsv.split(",")) {
                if (!a.isEmpty()) equippedAccs.add(a);
            }
        }

        GuiGraphicsExtractor gg = (GuiGraphicsExtractor)(Object) this;
        Minecraft mc = Minecraft.getInstance();

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int maxTextWidth = Math.max(120, screenW - MODEL_BOX - MODEL_TEXT_GAP - 40);
        List<FormattedCharSequence> lines = font.split(tooltipText, maxTextWidth);
        if (lines.isEmpty()) return;

        int textWidth = 0;
        for (FormattedCharSequence line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        int textBlockH = lines.size() * font.lineHeight;
        int innerW = MODEL_BOX + MODEL_TEXT_GAP + textWidth;
        int innerH = Math.max(MODEL_BOX, textBlockH);

        int boxX = mouseX + 12;
        int boxY = mouseY - innerH - 12;
        if (boxX + innerW + INNER_PAD * 2 > screenW) boxX = screenW - innerW - INNER_PAD * 2 - 4;
        if (boxY < 4) boxY = mouseY + 12;
        if (boxY + innerH + INNER_PAD * 2 > screenH) boxY = screenH - innerH - INNER_PAD * 2 - 4;
        if (boxX < 4) boxX = 4;

        gg.nextStratum();
        hats$drawTooltipBackground(gg, boxX, boxY, innerW, innerH);

        HatPart part = def.asHatPart(1);
        part.isShowing = true;
        for (HatPart acc : part.accessories) acc.isShowing = equippedAccs.contains(acc.name);

        int hatCenterX = boxX + MODEL_BOX / 2;
        int hatCenterY = boxY + innerH / 2;
        hats$drawHatModel(gg, part, hatCenterX, hatCenterY);

        int textX = boxX + MODEL_BOX + MODEL_TEXT_GAP;
        int textStartY = boxY + (innerH - textBlockH) / 2;
        for (int i = 0; i < lines.size(); i++) {
            gg.text(font, lines.get(i), textX, textStartY + i * font.lineHeight, 0xFFFFFFFF);
        }

        ci.cancel();
    }

    private void hats$drawTooltipBackground(GuiGraphicsExtractor gg, int x, int y, int innerW, int innerH)
    {
        int x0 = x - INNER_PAD;
        int y0 = y - INNER_PAD;
        int x1 = x + innerW + INNER_PAD;
        int y1 = y + innerH + INNER_PAD;

        int bg = 0xF0100010;
        int outline1 = 0x505000FF;
        int outline2 = 0x5028007F;

        gg.fill(x0, y0 - 1, x1, y0, bg);
        gg.fill(x0, y1, x1, y1 + 1, bg);
        gg.fill(x0, y0, x1, y1, bg);
        gg.fill(x0 - 1, y0, x0, y1, bg);
        gg.fill(x1, y0, x1 + 1, y1, bg);

        gg.fillGradient(x0, y0 + 1, x0 + 1, y1 - 1, outline1, outline2);
        gg.fillGradient(x1 - 1, y0 + 1, x1, y1 - 1, outline1, outline2);
        gg.fill(x0, y0, x1, y0 + 1, outline1);
        gg.fill(x0, y1 - 1, x1, y1, outline2);
    }

    private void hats$drawHatModel(GuiGraphicsExtractor gg, HatPart part, int cx, int cy)
    {
        float yaw = (System.currentTimeMillis() % 6000L) / 6000f * 360f;
        int half = MODEL_BOX / 2;
        HatModelRenderState.submit(gg, part, yaw, cx - half, cy - half, cx + half, cy + half, MODEL_BOX * 0.5f);
    }
}
