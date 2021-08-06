package fr.noclone.lockdown.network;
import fr.noclone.lockdown.Safe.ContainerSafe;
import fr.noclone.lockdown.Safe.ContainerSafeLocked;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncSafeClient {

    private String correctPassword;

    private Boolean isUnlocked;

    private UUID owner;

    public PacketSyncSafeClient(Boolean isUnlocked, String correctPassword, UUID owner)
    {
        this.correctPassword = correctPassword;
        this.isUnlocked = isUnlocked;
        this.owner = owner;
    }

    public static void encode(PacketSyncSafeClient packet, PacketBuffer buf)
    {
        buf.writeUtf(packet.correctPassword);
        buf.writeBoolean(packet.isUnlocked);
        buf.writeUUID(packet.owner);
    }

    public static PacketSyncSafeClient decode(PacketBuffer buf)
    {
        String correctPassword = buf.readUtf();
        Boolean isUnlocked = buf.readBoolean();
        UUID owner = buf.readUUID();
        PacketSyncSafeClient instance = new PacketSyncSafeClient(isUnlocked, correctPassword, owner);
        return instance;
    }

    public static void handle(PacketSyncSafeClient packet, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            PlayerEntity playerEntity = Minecraft.getInstance().player;

            if(playerEntity != null)
            {
                Container container = playerEntity.containerMenu;
                if(container instanceof ContainerSafeLocked)
                {
                    ((ContainerSafeLocked) container).sync(packet.isUnlocked, packet.correctPassword, packet.owner);
                }
                if(container instanceof ContainerSafe)
                {
                    ((ContainerSafe) container).sync(packet.isUnlocked, packet.correctPassword, packet.owner);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
