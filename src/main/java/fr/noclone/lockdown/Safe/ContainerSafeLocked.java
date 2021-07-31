package fr.noclone.lockdown.Safe;

import fr.noclone.lockdown.init.ModContainerTypes;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;

public class ContainerSafeLocked extends Container {

    private IIntArray fields;

    public IIntArray getFields() {
        return fields;
    }

    private TileEntitySafe tileEntitySafe;


    public ContainerSafeLocked(int id, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        this(id, playerInventory, new TileEntitySafe(), getArray(buffer));
    }

    private static IIntArray getArray(PacketBuffer buffer) {
        IIntArray array = new IntArray(buffer.readByte());
        array.set(1,buffer.readInt());
        array.set(2,buffer.readInt());
        array.set(3,buffer.readInt());
        return array;
    }

    public ContainerSafeLocked(int id, PlayerInventory playerInventory, TileEntitySafe tileEntitySafe, IIntArray fields)
    {
        super(ModContainerTypes.SAFE_LOCKED.get(), id);
        this.tileEntitySafe = tileEntitySafe;
        this.fields = fields;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public void sync(Boolean isUnlocked, String correctPassword)
    {
        tileEntitySafe.setUnlocked(isUnlocked);
        tileEntitySafe.setCorrectPassword(correctPassword);
    }
}
