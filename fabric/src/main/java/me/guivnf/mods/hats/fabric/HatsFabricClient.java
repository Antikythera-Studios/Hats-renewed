package me.guivnf.mods.hats.fabric;

import me.guivnf.mods.hats.HatsMod;
import me.guivnf.mods.hats.client.gui.HatModelPipRenderer;
import me.guivnf.mods.hats.client.gui.PlacementViewportRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;

@Environment(EnvType.CLIENT)
public class HatsFabricClient
        implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        HatsMod.clientInit();
        PictureInPictureRendererRegistry.register(context ->
            new HatModelPipRenderer(context.minecraft().renderBuffers().bufferSource()));
        PictureInPictureRendererRegistry.register(context ->
            new PlacementViewportRenderer(context.minecraft().renderBuffers().bufferSource()));
    }
}
