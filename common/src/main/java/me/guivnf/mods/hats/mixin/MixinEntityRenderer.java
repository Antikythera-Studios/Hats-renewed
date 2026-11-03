package me.guivnf.mods.hats.mixin;

import me.guivnf.mods.hats.HatsMod;
import me.guivnf.mods.hats.client.cache.ClientHatCache;
import me.guivnf.mods.hats.client.render.HatRenderStateAccess;
import me.guivnf.mods.hats.client.render.LayerHat;
import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.placement.HatPlacementRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer
{
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V", at = @At("TAIL"))
    private void hats$extractHat(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci)
    {
        HatRenderStateAccess access = (HatRenderStateAccess) state;
        if (!(entity instanceof LivingEntity living)) {
            access.hats$setHatData(null, null, null, false, false);
            return;
        }

        HatPart hat = ClientHatCache.getEntityHat(living.getUUID());
        if (hat == null || !hat.isShowing) {
            access.hats$setHatData(null, null, null, false, false);
            return;
        }

        boolean hidden = LayerHat.isOwnFirstPersonView(living)
            || (living.isInvisible() && !HatsMod.getConfig().renderOnInvisible);
        access.hats$setHatData(hat, HatPlacementRegistry.get(living), living, hidden,
            LayerHat.RENDERING_HAT_GUI_PREVIEW.get());
    }
}
