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

    private final IInventory inventory;
    private IIntArray fields;

    public ContainerSafeLocked(int id, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        this(id, playerInventory, new TileEntitySafe(), new IntArray(buffer.readByte()));
    }

    public ContainerSafeLocked(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields)
    {
        super(ModContainerTypes.SAFE_LOCKED.get(), id);
        this.inventory = inventory;
        this.fields = fields;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
