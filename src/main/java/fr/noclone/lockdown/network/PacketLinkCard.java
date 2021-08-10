package fr.noclone.lockdown.network;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.paymentterminal.TileEntityPaymentTerminal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketLinkCard {

    BlockPos pos;

    public PacketLinkCard(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(PacketLinkCard packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
    }

    public static PacketLinkCard decode(PacketBuffer buf)
    {
        return new PacketLinkCard(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
    }

    public static void handle(PacketLinkCard packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityBankServer)
        {
            ((TileEntityBankServer) te).LinkCard();
        }
        ctx.get().setPacketHandled(true);
    }
}
