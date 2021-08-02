package fr.noclone.lockdown.network;

import fr.noclone.lockdown.LockDown;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.UUID;

public class Messages {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LockDown.MODID, "lockdown"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerNetworkPackets()
    {
        INSTANCE.messageBuilder(PacketSyncSafe.class, 0)
                .encoder(PacketSyncSafe::encode)
                .decoder(PacketSyncSafe::decode)
                .consumer(PacketSyncSafe::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncBankServer.class, 0)
                .encoder(PacketSyncBankServer::encode)
                .decoder(PacketSyncBankServer::decode)
                .consumer(PacketSyncBankServer::handle)
                .add();

    }

    public static void sendToPlayer(ServerPlayerEntity player, boolean isUnlocked, String correctPassword, UUID owner)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncSafe(isUnlocked, correctPassword, owner));
    }

    public static void sendToEveryone(boolean isUnlocked, String correctPassword, UUID owner)
    {
        INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketSyncSafe(isUnlocked, correctPassword, owner));
    }
}
