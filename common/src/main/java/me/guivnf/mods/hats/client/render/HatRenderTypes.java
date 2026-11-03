package me.guivnf.mods.hats.client.render;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class HatRenderTypes
{
    private HatRenderTypes() {}

    private static final Function<Identifier, RenderType> HAT_TRANSLUCENT =
            Util.memoize(texture -> RenderType.create("hat_entity_translucent_no_cull",
                RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
                    .withTexture("Sampler0", texture)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .createRenderSetup()));

    private static final Function<Identifier, RenderType> HAT_TRANSLUCENT_CULL =
            Util.memoize(texture -> RenderType.create("hat_entity_translucent",
                RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
                    .withTexture("Sampler0", texture)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .createRenderSetup()));

    public static RenderType hatTranslucent(Identifier texture)
    {
        return HAT_TRANSLUCENT.apply(texture);
    }

    public static RenderType hatTranslucentCull(Identifier texture)
    {
        return HAT_TRANSLUCENT_CULL.apply(texture);
    }
}
