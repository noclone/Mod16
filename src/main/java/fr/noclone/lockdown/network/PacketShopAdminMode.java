package fr.noclone.lockdown.network;

import fr.noclone.lockdown.shop.TileEntityShop;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketShopAdminMode {

    BlockPos pos;

    boolean admin;

    public PacketShopAdminMode(BlockPos pos, boolean admin) {
        this.pos = pos;
        this.admin = admin;
    }

    public static void encode(PacketShopAdminMode packet, PacketBuffer buf)
    {
        buf.writeInt(packet.pos.getX());
        buf.writeInt(packet.pos.getY());
        buf.writeInt(packet.pos.getZ());
        buf.writeBoolean(packet.admin);
    }

    public static PacketShopAdminMode decode(PacketBuffer buf)
    {
        return new PacketShopAdminMode(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readBoolean());
    }

    public static void handle(PacketShopAdminMode packet, Supplier<NetworkEvent.Context> ctx)
    {
        PlayerEntity playerEntity = ctx.get().getSender();
        TileEntity te = playerEntity.level.getBlockEntity(packet.pos);
        if(te instanceof TileEntityShop)
        {
            ((TileEntityShop) te).setAdminMode(packet.admin);
        }
        ctx.get().setPacketHandled(true);
    }
}
