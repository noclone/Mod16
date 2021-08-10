package fr.noclone.lockdown.network;

import fr.noclone.lockdown.shop.TileEntityShop;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPriceChanged {

    BlockPos pos;
    int index;
    int value;

    public PacketPriceChanged(BlockPos blockPos, int index, int value) {
        this.pos = blockPos;
        this.index = index;
        this.value = value;
    }

    public static void encode(PacketPriceChanged packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
        buf.writeInt(packet.index);
        buf.writeInt(packet.value);
    }

    public static PacketPriceChanged decode(PacketBuffer buf)
    {
        return new PacketPriceChanged(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readInt());
    }

    public static void handle(PacketPriceChanged packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityShop)
        {
            ((TileEntityShop) te).PriceChanged(packet.index, packet.value);
        }
        ctx.get().setPacketHandled(true);
    }
}
