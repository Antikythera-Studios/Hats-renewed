package me.guivnf.mods.hats.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public record HatLauncherOffhandProperty() implements ConditionalItemModelProperty
{
    public static final MapCodec<HatLauncherOffhandProperty> MAP_CODEC = MapCodec.unit(new HatLauncherOffhandProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed,
            ItemDisplayContext displayContext)
    {
        if (owner == null) return false;

        ItemStack offhand = owner.getOffhandItem();
        if (offhand == stack) return true;

        ItemStack mainhand = owner.getMainHandItem();
        return mainhand != stack && offhand.getItem() == stack.getItem()
            && ItemStack.isSameItemSameComponents(offhand, stack);
    }

    @Override
    public MapCodec<HatLauncherOffhandProperty> type()
    {
        return MAP_CODEC;
    }
}
