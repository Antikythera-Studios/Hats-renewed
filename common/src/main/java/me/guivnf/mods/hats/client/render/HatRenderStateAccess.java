package me.guivnf.mods.hats.client.render;

import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.placement.HatPlacementInfo;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;

public interface HatRenderStateAccess
{
    @Nullable
    HatPart hats$getHat();

    @Nullable
    HatPlacementInfo hats$getPlacement();

    @Nullable
    LivingEntity hats$getEntity();

    boolean hats$isHatHidden();

    boolean hats$isGuiPreview();

    boolean hats$isLayerRendered();

    void hats$setLayerRendered(boolean rendered);

    void hats$setHatData(@Nullable HatPart hat, @Nullable HatPlacementInfo placement, @Nullable LivingEntity entity,
            boolean hidden, boolean guiPreview);
}
