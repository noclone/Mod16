package fr.noclone.lockdown.network;
import fr.noclone.lockdown.bankserver.ContainerBankServer;
import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.NetworkEvent;
import org.lwjgl.BufferUtils;

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
        return new PacketSyncBankServer(buf.readUUID());
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
