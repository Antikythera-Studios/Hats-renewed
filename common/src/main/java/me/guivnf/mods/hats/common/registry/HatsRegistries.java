package me.guivnf.mods.hats.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.guivnf.mods.hats.HatsMod;
import me.guivnf.mods.hats.common.entity.HatEntity;
import me.guivnf.mods.hats.common.item.HatLauncherItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;

public class HatsRegistries
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(HatsMod.MOD_ID, Registries.ENTITY_TYPE);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(HatsMod.MOD_ID, Registries.ITEM);

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(HatsMod.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<EntityType<HatEntity>> HAT_ENTITY =
            ENTITY_TYPES.register("hat", () ->
                EntityType.Builder.<HatEntity>of(HatEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(HatsMod.MOD_ID, "hat")))
            );

    public static final RegistrySupplier<HatLauncherItem> HAT_LAUNCHER =
            ITEMS.register("hat_launcher", () -> new HatLauncherItem(new Item.Properties().stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HatsMod.MOD_ID, "hat_launcher")))));

    public static final RegistrySupplier<SoundEvent> POOF =
            SOUND_EVENTS.register("poof", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(HatsMod.MOD_ID, "poof")));

    public static final RegistrySupplier<SoundEvent> TUBE =
            SOUND_EVENTS.register("tube", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(HatsMod.MOD_ID, "tube")));

    public static final RegistrySupplier<SoundEvent> BONK =
            SOUND_EVENTS.register("bonk", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(HatsMod.MOD_ID, "bonk")));

    public static void register()
    {
        ENTITY_TYPES.register();
        ITEMS.register();
        SOUND_EVENTS.register();
    }
}
