package fr.noclone.lockdown.network;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.shop.TileEntityShop;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangeGhost {

    BlockPos pos;
    int index;
    boolean place;
    ItemStack stack;

    public PacketChangeGhost(BlockPos blockPos, int index, boolean place, ItemStack stack) {
        this.pos = blockPos;
        this.index = index;
        this.place = place;
        this.stack = stack;
    }

    public static void encode(PacketChangeGhost packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
        buf.writeInt(packet.index);
        buf.writeBoolean(packet.place);
        buf.writeItem(packet.stack);
    }

    public static PacketChangeGhost decode(PacketBuffer buf)
    {
        return new PacketChangeGhost(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readBoolean(), buf.readItem());
    }

    public static void handle(PacketChangeGhost packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityShop)
        {
            ((TileEntityShop) te).ChangeGhost(packet.index, packet.place, packet.stack);
        }
        ctx.get().setPacketHandled(true);
    }
}
