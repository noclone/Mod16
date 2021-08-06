package fr.noclone.lockdown.Safe;

import fr.noclone.lockdown.init.ModContainerTypes;
import fr.noclone.lockdown.init.ModTileEntities;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncSafe;
import net.minecraft.client.Minecraft;
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

import java.util.UUID;

public class ContainerSafe extends Container {

    private final IInventory inventory;
    private IIntArray fields;

    public IIntArray getFields() {
        return fields;
    }

    private TileEntitySafe tileEntitySafe;


    public ContainerSafe(int id, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        this(id, playerInventory, getTileEntity(buffer), getArray(buffer));
    }

    private static TileEntitySafe getTileEntity(PacketBuffer buffer) {
        TileEntitySafe tileEntitySafe = new TileEntitySafe();
        tileEntitySafe.setOwner(buffer.readUUID());
        return tileEntitySafe;
    }

    private static IIntArray getArray(PacketBuffer buffer) {
        IIntArray array = new IntArray(buffer.readByte());
        array.set(1,buffer.readInt());
        array.set(2,buffer.readInt());
        array.set(3,buffer.readInt());
        return array;
    }

    public ContainerSafe(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields)
    {
        super(ModContainerTypes.SAFE.get(), id);
        this.inventory = inventory;
        this.fields = fields;
        this.tileEntitySafe = (TileEntitySafe) inventory;

        addSafeInventory();
        addPlayerInventory(playerInventory);

    }

    private void addSafeInventory()
    {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                int index = x + y * 9;
                int posX = 8 + x * 18;
                int posY = 18 + y * 18;
                this.addSlot(new Slot(this.inventory, index, posX, posY));
            }
        }
    }

    private void addPlayerInventory(PlayerInventory playerInventory)
    {

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                int index = x + y * 9 + 9;
                int posX = 8 + x * 18;
                int posY = 84 + y * 18;
                this.addSlot(new Slot(playerInventory, index, posX, posY));
            }
        }

        // Player hotbar
        for (int x = 0; x < 9; ++x) {
            int index = x;
            int posX = 8 + x * 18;
            int posY = 142;
            this.addSlot(new Slot(playerInventory, index, posX, posY));
        }
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

    public void sync(Boolean isUnlocked, String correctPassword, UUID owner)
    {
        tileEntitySafe.setUnlocked(isUnlocked);
        tileEntitySafe.setCorrectPassword(correctPassword);
        tileEntitySafe.setOwner(owner);
    }
}
