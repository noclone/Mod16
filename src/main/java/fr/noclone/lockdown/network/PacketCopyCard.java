package fr.noclone.lockdown.network;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCopyCard {

    BlockPos pos;
    int i;

    public PacketCopyCard(BlockPos pos, int i) {
        this.pos = pos;
        this.i = i;
    }

    public static void encode(PacketCopyCard packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
        buf.writeInt(packet.i);
    }

    public static PacketCopyCard decode(PacketBuffer buf)
    {
        return new PacketCopyCard(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt());
    }

    public static void handle(PacketCopyCard packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityBankServer)
        {
            ((TileEntityBankServer) te).CopyCard(packet.i);
        }
        ctx.get().setPacketHandled(true);
    }
}
