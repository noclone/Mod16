package fr.noclone.lockdown.network;
import fr.noclone.lockdown.Safe.ContainerSafe;
import fr.noclone.lockdown.Safe.ContainerSafeLocked;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncSafe{

    private String correctPassword;

    private Boolean isUnlocked;

    public PacketSyncSafe(Boolean isUnlocked, String correctPassword)
    {
        this.correctPassword = correctPassword;
        this.isUnlocked = isUnlocked;
    }

    public static void encode(PacketSyncSafe packet, PacketBuffer buf)
    {
        buf.writeUtf(packet.correctPassword);
        buf.writeBoolean(packet.isUnlocked);
    }

    public static PacketSyncSafe decode(PacketBuffer buf)
    {
        String correctPassword = buf.readUtf();
        Boolean isUnlocked = buf.readBoolean();
        PacketSyncSafe instance = new PacketSyncSafe(isUnlocked, correctPassword);
        return instance;
    }

    public static void handle(PacketSyncSafe packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        if(playerEntity == null) {
            playerEntity = Minecraft.getInstance().player;
            if(playerEntity.containerMenu instanceof ContainerSafe)
            {
                ((ContainerSafe) playerEntity.containerMenu).sync(packet.isUnlocked, packet.correctPassword);
            }
        }

        Container container = playerEntity.containerMenu;
        if(container instanceof ContainerSafeLocked)
        {
            ((ContainerSafeLocked) container).sync(packet.isUnlocked, packet.correctPassword);
        }
        ctx.get().setPacketHandled(true);
    }


}
