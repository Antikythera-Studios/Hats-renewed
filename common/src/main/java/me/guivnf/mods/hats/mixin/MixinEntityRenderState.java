package me.guivnf.mods.hats.mixin;

import me.guivnf.mods.hats.client.render.HatRenderStateAccess;
import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.placement.HatPlacementInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class MixinEntityRenderState implements HatRenderStateAccess
{
    @Unique
    private @Nullable HatPart hats$hat;

    @Unique
    private @Nullable HatPlacementInfo hats$placement;

    @Unique
    private @Nullable LivingEntity hats$entity;

    @Unique
    private boolean hats$hidden;

    @Unique
    private boolean hats$guiPreview;

    @Unique
    private boolean hats$layerRendered;

    @Override
    public @Nullable HatPart hats$getHat()
    {
        return hats$hat;
    }

    @Override
    public @Nullable HatPlacementInfo hats$getPlacement()
    {
        return hats$placement;
    }

    @Override
    public @Nullable LivingEntity hats$getEntity()
    {
        return hats$entity;
    }

    @Override
    public boolean hats$isHatHidden()
    {
        return hats$hidden;
    }

    @Override
    public boolean hats$isGuiPreview()
    {
        return hats$guiPreview;
    }

    @Override
    public boolean hats$isLayerRendered()
    {
        return hats$layerRendered;
    }

    @Override
    public void hats$setLayerRendered(boolean rendered)
    {
        hats$layerRendered = rendered;
    }

    @Override
    public void hats$setHatData(@Nullable HatPart hat, @Nullable HatPlacementInfo placement, @Nullable LivingEntity entity,
            boolean hidden, boolean guiPreview)
    {
        hats$hat = hat;
        hats$placement = placement;
        hats$entity = entity;
        hats$hidden = hidden;
        hats$guiPreview = guiPreview;
        hats$layerRendered = false;
    }
}
