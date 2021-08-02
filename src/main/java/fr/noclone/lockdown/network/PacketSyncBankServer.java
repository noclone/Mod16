package fr.noclone.lockdown.network;
import fr.noclone.lockdown.bankserver.ContainerBankServer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncBankServer {

    private UUID owner;

    public PacketSyncBankServer(UUID owner)
    {
        this.owner = owner;
    }

    public static void encode(PacketSyncBankServer packet, PacketBuffer buf)
    {
        buf.writeUUID(packet.owner);
    }

    public static PacketSyncBankServer decode(PacketBuffer buf)
    {
        UUID owner = buf.readUUID();
        PacketSyncBankServer instance = new PacketSyncBankServer(owner);
        return instance;
    }

    public static void handle(PacketSyncBankServer packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        if(playerEntity == null) {
            playerEntity = Minecraft.getInstance().player;
            if(playerEntity.containerMenu instanceof ContainerBankServer)
            {
                ((ContainerBankServer) playerEntity.containerMenu).sync(packet.owner);
                ctx.get().setPacketHandled(true);
                return;
            }
        }

        Container container = playerEntity.containerMenu;
        if(container instanceof ContainerBankServer)
        {
            ((ContainerBankServer) container).sync(packet.owner);
        }
        ctx.get().setPacketHandled(true);
    }


}
