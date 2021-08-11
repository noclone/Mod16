package fr.noclone.lockdown.network;
import fr.noclone.lockdown.bankserver.ContainerBankServer;
import fr.noclone.lockdown.clearer.ContainerClearer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncClearer {

    private UUID owner;

    public PacketSyncClearer(UUID owner)
    {
        this.owner = owner;

    }

    public static void encode(PacketSyncClearer packet, PacketBuffer buf)
    {
        buf.writeUUID(packet.owner);
    }

    public static PacketSyncClearer decode(PacketBuffer buf)
    {
        return new PacketSyncClearer(buf.readUUID());
    }

    public static void handle(PacketSyncClearer packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        if(playerEntity == null) {
        }

        Container container = playerEntity.containerMenu;
        if(container instanceof ContainerClearer)
        {
            ((ContainerClearer) container).sync(packet.owner);
        }
        ctx.get().setPacketHandled(true);
    }


}
