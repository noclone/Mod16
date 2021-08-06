package fr.noclone.lockdown.network;

import fr.noclone.lockdown.LockDown;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.lwjgl.system.windows.MSG;

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
        int id = 0;
        INSTANCE.messageBuilder(PacketSyncSafe.class, id++)
                .encoder(PacketSyncSafe::encode)
                .decoder(PacketSyncSafe::decode)
                .consumer(PacketSyncSafe::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncSafeClient.class, id++)
                .encoder(PacketSyncSafeClient::encode)
                .decoder(PacketSyncSafeClient::decode)
                .consumer(PacketSyncSafeClient::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncBankServer.class, id++)
                .encoder(PacketSyncBankServer::encode)
                .decoder(PacketSyncBankServer::decode)
                .consumer(PacketSyncBankServer::handle)
                .add();




    }
}
