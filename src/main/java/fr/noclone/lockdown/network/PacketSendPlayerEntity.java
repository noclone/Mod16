package fr.noclone.lockdown.network;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendPlayerEntity {

    BlockPos pos;

    public PacketSendPlayerEntity(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(PacketSendPlayerEntity packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
    }

    public static PacketSendPlayerEntity decode(PacketBuffer buf)
    {
        return new PacketSendPlayerEntity(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
    }

    public static void handle(PacketSendPlayerEntity packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityClearer)
        {
            ((TileEntityClearer) te).clear(playerEntity);
        }
        ctx.get().setPacketHandled(true);
    }
}
