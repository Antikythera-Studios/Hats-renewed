package me.guivnf.mods.hats.mixin;

import me.guivnf.mods.hats.common.world.HatsSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void hats$forgetRemovedMobHat(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (reason != Entity.RemovalReason.KILLED && reason != Entity.RemovalReason.DISCARDED)
            return;
        if (!(self instanceof Mob) || !(self.level() instanceof ServerLevel level))
            return;
        if (!level.getServer().isSameThread())
            return;

        HatsSavedData data = HatsSavedData.get(level);
        if (data.getEntityHat(self.getUUID()) != null)
            data.setEntityHat(self.getUUID(), null);
    }
}
