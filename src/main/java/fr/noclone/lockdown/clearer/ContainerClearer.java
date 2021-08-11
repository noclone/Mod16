package fr.noclone.lockdown.clearer;

import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.init.ModContainerTypes;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSendPlayerEntity;
import fr.noclone.lockdown.shop.TileEntityShop;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;

import java.util.UUID;

public class ContainerClearer extends Container{

    public IInventory getInventory() {
        return inventory;
    }

    private final IInventory inventory;


    private IIntArray fields;

    public IIntArray getFields() {
        return fields;
    }

    private TileEntityClearer tileEntityClearer;

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    private PlayerInventory playerInventory;


    public ContainerClearer(int id, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        this(id, playerInventory, getTileEntity(buffer), getArray(buffer));
    }

    private static TileEntityClearer getTileEntity(PacketBuffer buffer) {
        TileEntityClearer tileentity = new TileEntityClearer();
        tileentity.setOwner(buffer.readUUID());
        return tileentity;
    }

    private static IIntArray getArray(PacketBuffer buffer) {
        IIntArray array = new IntArray(buffer.readByte());
        array.set(1,buffer.readInt());
        array.set(2,buffer.readInt());
        array.set(3,buffer.readInt());
        return array;
    }

    public ContainerClearer(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields)
    {
        super(ModContainerTypes.CLEARER.get(), id);
        this.inventory = inventory;
        this.fields = fields;
        this.tileEntityClearer = (TileEntityClearer) inventory;
        this.playerInventory = playerInventory;

        addClearerInventory();
        addPlayerInventory(playerInventory);
    }

    private void addClearerInventory()
    {
        this.addSlot(new Slot(this.inventory, 0, 80, 35)
        {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if(stack.getItem() instanceof CreditCard)
                {
                    return true;
                }
                return false;
            }
        });
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
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
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

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.inventory.stillValid(player);
    }

    public void sync(UUID owner)
    {
        tileEntityClearer.setOwner(owner);
    }
}
