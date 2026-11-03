package me.guivnf.mods.hats.common.network.packet;

import net.minecraft.server.permissions.Permissions;
import dev.architectury.networking.NetworkManager;
import me.guivnf.mods.hats.common.hat.HatDefinition;
import me.guivnf.mods.hats.common.hat.HatPart;
import me.guivnf.mods.hats.common.hat.HatRegistry;
import me.guivnf.mods.hats.common.network.NetworkHelper;
import me.guivnf.mods.hats.common.world.HatsSavedData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;

public class PacketGiveHat
{
    public static RegistryFriendlyByteBuf encode(String hatName, String targetPlayerName)
    {
        RegistryFriendlyByteBuf buf = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), net.minecraft.core.RegistryAccess.EMPTY);
        buf.writeUtf(hatName);
        buf.writeUtf(targetPlayerName);
        return buf;
    }

    public static void handle(RegistryFriendlyByteBuf buf, NetworkManager.PacketContext context)
    {
        String hatName = buf.readUtf();
        String targetName = buf.readUtf();

        context.queue(() -> {
            ServerPlayer sender = (ServerPlayer) context.getPlayer();
            if (!sender.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) return;

            ServerPlayer target = sender.level().getServer().getPlayerList().getPlayerByName(targetName);
            if (target == null) return;

            HatDefinition def = HatRegistry.get(hatName);
            if (def == null) return;

            HatPart hat = def.asHatPart(1);
            HatsSavedData data = HatsSavedData.get(sender.level());
            data.addHatToInventory(target.getUUID(), hat);

            NetworkHelper.syncInventory(target, data);
            NetworkHelper.sendNewHatToast(target, hat, false);
        });
    }
}
