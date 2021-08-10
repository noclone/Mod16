package fr.noclone.lockdown.network;
import fr.noclone.lockdown.clearer.ContainerClearer;
import fr.noclone.lockdown.paymentterminal.ContainerPaymentTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncPaymentTerminal {

    private UUID owner;

    public PacketSyncPaymentTerminal(UUID owner)
    {
        this.owner = owner;
    }

    public static void encode(PacketSyncPaymentTerminal packet, PacketBuffer buf)
    {
        buf.writeUUID(packet.owner);
    }

    public static PacketSyncPaymentTerminal decode(PacketBuffer buf)
    {
        return new PacketSyncPaymentTerminal(buf.readUUID());
    }

    public static void handle(PacketSyncPaymentTerminal packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        if(playerEntity == null) {
            playerEntity = Minecraft.getInstance().player;
            if(playerEntity.containerMenu instanceof ContainerPaymentTerminal)
            {
                ((ContainerPaymentTerminal) playerEntity.containerMenu).sync(packet.owner);
                ctx.get().setPacketHandled(true);
                return;
            }
        }

        Container container = playerEntity.containerMenu;
        if(container instanceof ContainerPaymentTerminal)
        {
            ((ContainerPaymentTerminal) container).sync(packet.owner);
        }
        ctx.get().setPacketHandled(true);
    }


}
