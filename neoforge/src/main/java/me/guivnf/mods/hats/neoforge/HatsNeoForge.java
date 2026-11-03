package me.guivnf.mods.hats.neoforge;

import me.guivnf.mods.hats.HatsMod;
import me.guivnf.mods.hats.client.gui.HatModelPipRenderer;
import me.guivnf.mods.hats.client.gui.HatModelRenderState;
import me.guivnf.mods.hats.client.gui.PlacementViewportRenderState;
import me.guivnf.mods.hats.client.gui.PlacementViewportRenderer;
import me.guivnf.mods.hats.common.registry.HatsRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@Mod(HatsMod.MOD_ID)
public class HatsNeoForge
{
    public HatsNeoForge(IEventBus modBus, ModContainer container, Dist dist)
    {
        HatsRegistries.register();
        HatsMod.init();

        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onLoadComplete);

        if (dist == Dist.CLIENT) {
            modBus.addListener(this::onClientSetup);
            modBus.addListener(this::onRegisterRenderers);
            modBus.addListener(this::onRegisterPipRenderers);
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        HatsMod.clientInit();
    }

    @SuppressWarnings("unchecked")
    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(
            (EntityType<me.guivnf.mods.hats.common.entity.HatEntity>)
                HatsRegistries.HAT_ENTITY.get(),
            me.guivnf.mods.hats.client.render.HatEntityRenderer::new);
    }

    private void onRegisterPipRenderers(RegisterPictureInPictureRenderersEvent event)
    {
        event.register(HatModelRenderState.class, HatModelPipRenderer::new);
        event.register(PlacementViewportRenderState.class, PlacementViewportRenderer::new);
    }

    private void onLoadComplete(FMLLoadCompleteEvent event)
    {
        HatsMod.onLoadComplete();
    }
}
