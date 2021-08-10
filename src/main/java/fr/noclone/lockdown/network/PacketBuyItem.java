package fr.noclone.lockdown.network;

import fr.noclone.lockdown.shop.TileEntityShop;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBuyItem {

    BlockPos pos;
    int index;
    boolean shift;

    public PacketBuyItem(BlockPos blockPos, int index, boolean shift) {
        this.pos = blockPos;
        this.index = index;
        this.shift = shift;
    }

    public static void encode(PacketBuyItem packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
        buf.writeInt(packet.index);
        buf.writeBoolean(packet.shift);
    }

    public static PacketBuyItem decode(PacketBuffer buf)
    {
        return new PacketBuyItem(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readBoolean());
    }

    public static void handle(PacketBuyItem packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityShop)
        {
            ((TileEntityShop) te).BuyItem(packet.index, packet.shift);
        }
        ctx.get().setPacketHandled(true);
    }
}
