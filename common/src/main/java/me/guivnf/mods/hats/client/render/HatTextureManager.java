package me.guivnf.mods.hats.client.render;

import me.guivnf.mods.hats.HatsMod;
import me.guivnf.mods.hats.common.hat.HatDefinition;
import me.guivnf.mods.hats.common.hat.HatPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class HatTextureManager
{
    private static final Map<String, Identifier> TEXTURE_CACHE = new HashMap<>();

    @Nullable
    public static Identifier getTexture(HatDefinition def, HatPart part)
    {
        if (def.textureBytes == null || def.textureBytes.length == 0) return null;

        float hShift = part.hsb[0];
        float sShift = part.hsb[1];
        float bShift = part.hsb[2];

        boolean hasHsbShift = hShift != 0f || sShift != 0f || bShift != 0f;

        String cacheKey = def.getFullName() + "/" + (hasHsbShift ? hShift + "H" + sShift + "S" + bShift + "B" : "base");
        return TEXTURE_CACHE.computeIfAbsent(cacheKey, k -> uploadTexture(k, def.textureBytes, hShift, sShift, bShift));
    }

    @Nullable
    private static Identifier uploadTexture(String key, byte[] pngBytes, float hShift, float sShift, float bShift)
    {
        try {
            com.mojang.blaze3d.platform.NativeImage image =
                com.mojang.blaze3d.platform.NativeImage.read(new ByteArrayInputStream(pngBytes));

            if (hShift != 0f || sShift != 0f || bShift != 0f) {
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        int argb = image.getPixel(x, y);
                        int alpha = (argb >> 24) & 0xff;
                        if (alpha == 0) continue;

                        int red   = (argb >> 16) & 0xff;
                        int green = (argb >> 8)  & 0xff;
                        int blue  =  argb         & 0xff;

                        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                        hsb[0] = (hsb[0] + hShift) % 1f;
                        if (hsb[0] < 0) hsb[0] += 1f;
                        hsb[1] = Math.max(0, Math.min(1, hsb[1] * (1 + sShift)));
                        hsb[2] = Math.max(0, Math.min(1, hsb[2] * (1 + bShift)));

                        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8)  & 0xff;
                        int b =  rgb         & 0xff;
                        image.setPixel(x, y, (alpha << 24) | (r << 16) | (g << 8) | b);
                    }
                }
            }

            DynamicTexture dynamicTexture = new DynamicTexture(() -> "hats:" + key, image);
            Identifier location = Identifier.fromNamespaceAndPath(HatsMod.MOD_ID, "dynamic/" + key.hashCode());
            Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
            return location;
        } catch (Exception e) {
            HatsMod.LOGGER.error("Failed to upload texture for hat key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    public static void evict(String hatName)
    {
        TEXTURE_CACHE.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(hatName + "/")) {
                Minecraft.getInstance().getTextureManager().release(entry.getValue());
                return true;
            }
            return false;
        });
    }

    public static void clearAll()
    {
        for (Identifier loc : TEXTURE_CACHE.values()) {
            Minecraft.getInstance().getTextureManager().release(loc);
        }
        TEXTURE_CACHE.clear();
    }
}
