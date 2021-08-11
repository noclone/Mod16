package fr.noclone.lockdown.network;
import fr.noclone.lockdown.Safe.ContainerSafe;
import fr.noclone.lockdown.Safe.ContainerSafeLocked;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncSafe{

    private String correctPassword;

    private Boolean isUnlocked;

    private UUID owner;

    public PacketSyncSafe(Boolean isUnlocked, String correctPassword, UUID owner)
    {
        this.correctPassword = correctPassword;
        this.isUnlocked = isUnlocked;
        this.owner = owner;
    }

    public static void encode(PacketSyncSafe packet, PacketBuffer buf)
    {
        buf.writeInt(packet.correctPassword.length());
        for(int i = 0; i < packet.correctPassword.length();  i++)
        {
            buf.writeChar(packet.correctPassword.charAt(i));
        }
        buf.writeBoolean(packet.isUnlocked);
        buf.writeUUID(packet.owner);
    }

    public static PacketSyncSafe decode(PacketBuffer buf)
    {
        int lenght = buf.readInt();
        String pwd = "";
        for(int i = 0; i < lenght;  i++)
        {
            pwd += buf.readChar();
        }
        Boolean isUnlocked = buf.readBoolean();
        UUID owner = buf.readUUID();
        PacketSyncSafe instance = new PacketSyncSafe(isUnlocked, pwd, owner);
        return instance;
    }

    public static void handle(PacketSyncSafe packet, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            PlayerEntity playerEntity = ctx.get().getSender();

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
