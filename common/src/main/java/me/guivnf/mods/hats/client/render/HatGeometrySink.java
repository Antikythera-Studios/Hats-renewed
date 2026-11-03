package me.guivnf.mods.hats.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

@FunctionalInterface
public interface HatGeometrySink
{
    void accept(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer geometry);

    default HatGeometrySink above()
    {
        return this;
    }

    static HatGeometrySink of(SubmitNodeCollector collector)
    {
        return ordered(collector, 0);
    }

    static HatGeometrySink of(MultiBufferSource buffers)
    {
        return (poseStack, renderType, geometry) -> geometry.render(poseStack.last(), buffers.getBuffer(renderType));
    }

    private static HatGeometrySink ordered(SubmitNodeCollector collector, int order)
    {
        return new HatGeometrySink()
        {
            @Override
            public void accept(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer geometry)
            {
                collector.order(order).submitCustomGeometry(poseStack, renderType, geometry);
            }

            @Override
            public HatGeometrySink above()
            {
                return ordered(collector, order + 1);
            }
        };
    }
}
