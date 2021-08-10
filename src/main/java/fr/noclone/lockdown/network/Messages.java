package fr.noclone.lockdown.network;

import fr.noclone.lockdown.LockDown;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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

        INSTANCE.messageBuilder(PacketSyncBankServer.class, id++)
                .encoder(PacketSyncBankServer::encode)
                .decoder(PacketSyncBankServer::decode)
                .consumer(PacketSyncBankServer::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncClearer.class, id++)
                .encoder(PacketSyncClearer::encode)
                .decoder(PacketSyncClearer::decode)
                .consumer(PacketSyncClearer::handle)
                .add();

        INSTANCE.messageBuilder(PacketLinkCard.class, id++)
                .encoder(PacketLinkCard::encode)
                .decoder(PacketLinkCard::decode)
                .consumer(PacketLinkCard::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncPaymentTerminal.class, id++)
                .encoder(PacketSyncPaymentTerminal::encode)
                .decoder(PacketSyncPaymentTerminal::decode)
                .consumer(PacketSyncPaymentTerminal::handle)
                .add();

        INSTANCE.messageBuilder(PacketPayInTerminal.class, id++)
                .encoder(PacketPayInTerminal::encode)
                .decoder(PacketPayInTerminal::decode)
                .consumer(PacketPayInTerminal::handle)
                .add();

        INSTANCE.messageBuilder(PacketDeleteCard.class, id++)
                .encoder(PacketDeleteCard::encode)
                .decoder(PacketDeleteCard::decode)
                .consumer(PacketDeleteCard::handle)
                .add();

        INSTANCE.messageBuilder(PacketChangeGhost.class, id++)
                .encoder(PacketChangeGhost::encode)
                .decoder(PacketChangeGhost::decode)
                .consumer(PacketChangeGhost::handle)
                .add();

        INSTANCE.messageBuilder(PacketPriceChanged.class, id++)
                .encoder(PacketPriceChanged::encode)
                .decoder(PacketPriceChanged::decode)
                .consumer(PacketPriceChanged::handle)
                .add();

        INSTANCE.messageBuilder(PacketBuyItem.class, id++)
                .encoder(PacketBuyItem::encode)
                .decoder(PacketBuyItem::decode)
                .consumer(PacketBuyItem::handle)
                .add();

        INSTANCE.messageBuilder(PacketLinkShopCard.class, id++)
                .encoder(PacketLinkShopCard::encode)
                .decoder(PacketLinkShopCard::decode)
                .consumer(PacketLinkShopCard::handle)
                .add();

        INSTANCE.messageBuilder(PacketShopAdminMode.class, id++)
                .encoder(PacketShopAdminMode::encode)
                .decoder(PacketShopAdminMode::decode)
                .consumer(PacketShopAdminMode::handle)
                .add();

        INSTANCE.messageBuilder(PacketCopyCard.class, id++)
                .encoder(PacketCopyCard::encode)
                .decoder(PacketCopyCard::decode)
                .consumer(PacketCopyCard::handle)
                .add();
    }
}
