package fr.noclone.lockdown.network;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.paymentterminal.TileEntityPaymentTerminal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPayInTerminal {


    BlockPos pos;

    int amount;

    public PacketPayInTerminal(BlockPos pos, int amount) {
        this.pos = pos;
        this.amount = amount;
    }

    public static void encode(PacketPayInTerminal packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
        buf.writeInt(packet.amount);
    }

    public static PacketPayInTerminal decode(PacketBuffer buf)
    {
        return new PacketPayInTerminal(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt());
    }

    public static void handle(PacketPayInTerminal packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityPaymentTerminal)
        {
            ((TileEntityPaymentTerminal) te).Pay(packet.amount);
        }
        ctx.get().setPacketHandled(true);
    }
}
