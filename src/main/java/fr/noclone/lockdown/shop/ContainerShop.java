package fr.noclone.lockdown.shop;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.init.ModContainerTypes;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketChangeGhost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContainerShop extends Container{

    public IInventory getInventory() {
        return inventory;
    }

    private final IInventory inventory;


    private IIntArray fields;

    public IIntArray getFields() {
        return fields;
    }

    private TileEntityShop tileEntityShop;

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    private PlayerInventory playerInventory;


    public ContainerShop(int id, PlayerInventory playerInventory, PacketBuffer buffer)
    {
        this(id, playerInventory, getTileEntity(buffer), getArray(buffer));
    }

    private static TileEntityShop getTileEntity(PacketBuffer buffer) {
        TileEntityShop tileentityshop = new TileEntityShop();
        tileentityshop.setOwner(buffer.readUUID());
        return tileentityshop;
    }

    private static IIntArray getArray(PacketBuffer buffer) {
        IIntArray array = new IntArray(buffer.readByte());
        array.set(1,buffer.readInt());
        array.set(2,buffer.readInt());
        array.set(3,buffer.readInt());
        return array;
    }

    public ContainerShop(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields)
    {
        super(ModContainerTypes.SHOP.get(), id);
        this.inventory = inventory;
        this.fields = fields;
        this.tileEntityShop = (TileEntityShop) inventory;
        this.playerInventory = playerInventory;

        if(tileEntityShop.getBlockPos().equals(BlockPos.ZERO))
            tileEntityShop.setPosition(new BlockPos(fields.get(1), fields.get(2), fields.get(3)));

        addShopInventory();
        addPlayerInventory(playerInventory);
    }

    private void addShopInventory()
    {
        this.addSlot(new Slot(this.inventory, 0, 134, 22)
        {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if(stack.getItem() instanceof CreditCard && stack.hasTag() && stack.getTag().contains("balance"))
                    return true;
                return false;
            }
        });

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                int index = x + y * 9 + 1;
                int posX = 8 + x * 18;
                int posY = 58 + y * 18;
                this.addSlot(new Slot(inventory, index, posX, posY){
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        if(!tileEntityShop.getOwner().equals(Minecraft.getInstance().player.getUUID()))
                            return false;
                        Messages.INSTANCE.sendToServer(new PacketChangeGhost(tileEntityShop.getBlockPos(), index, true, stack));
                        return false;
                    }

                    @Override
                    public boolean mayPickup(PlayerEntity playerEntity) {
                        return false;
                    }
                });
            }
        }
    }

    private void addPlayerInventory(PlayerInventory playerInventory)
    {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                int index = x + y * 9 + 9;
                int posX = 8 + x * 18;
                int posY = 124 + y * 18;
                this.addSlot(new Slot(playerInventory, index, posX, posY));
            }
        }

        // Player hotbar
        for (int x = 0; x < 9; ++x) {
            int index = x;
            int posX = 8 + x * 18;
            int posY = 182;
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
            if (index < 28) {
                if (!this.moveItemStackTo(itemstack1, 28, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 28, false)) {
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
        tileEntityShop.setOwner(owner);
    }
}
