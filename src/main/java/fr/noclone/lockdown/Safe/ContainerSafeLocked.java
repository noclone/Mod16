package fr.noclone.lockdown.Safe;

import fr.noclone.lockdown.init.ModContainerTypes;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncSafe;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;

import java.nio.charset.MalformedInputException;
import java.util.UUID;

public class ContainerSafeLocked extends Container {

    private IIntArray fields;

    public IIntArray getFields() {
        return fields;
    }

    public TileEntitySafe getTileEntitySafe() {
        return tileEntitySafe;
    }

    private TileEntitySafe tileEntitySafe;

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    private PlayerInventory playerInventory;

    public ContainerSafeLocked(int id, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        this(id, playerInventory, getTileEntity(buffer), getArray(buffer));
    }

    private static IIntArray getArray(PacketBuffer buffer) {
        IIntArray array = new IntArray(buffer.readByte());
        array.set(1,buffer.readInt());
        array.set(2,buffer.readInt());
        array.set(3,buffer.readInt());
        return array;
    }

    private static TileEntitySafe getTileEntity(PacketBuffer buffer) {
        TileEntitySafe tileEntitySafe = new TileEntitySafe();
        tileEntitySafe.setOwner(buffer.readUUID());
        return tileEntitySafe;
    }


    public ContainerSafeLocked(int id, PlayerInventory playerInventory, TileEntitySafe tileEntitySafe, IIntArray fields)
    {
        super(ModContainerTypes.SAFE_LOCKED.get(), id);
        this.tileEntitySafe = tileEntitySafe;
        this.fields = fields;
        this.playerInventory = playerInventory;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public void sync(Boolean isUnlocked, String correctPassword, UUID owner)
    {
        tileEntitySafe.setUnlocked(isUnlocked);
        tileEntitySafe.setCorrectPassword(correctPassword);
        tileEntitySafe.setOwner(owner);
    }
}
