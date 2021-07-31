package fr.noclone.lockdown.Safe;

import fr.noclone.lockdown.init.ModTileEntities;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncSafe;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class TileEntitySafe extends LockableTileEntity implements ISidedInventory{

    private NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers;

    private boolean isUnlocked = false;

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public String getCorrectPassword() {
        return correctPassword;
    }

    public void setCorrectPassword(String correctPassword) {
        this.correctPassword = correctPassword;
    }

    private String correctPassword = "1234";

    public TileEntitySafe() {
        super(ModTileEntities.SAFE_TILE_ENTITY.get());
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);

        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundNBT, this.items);
        correctPassword = compoundNBT.getString("correctPassword");
        isUnlocked = compoundNBT.getBoolean("isUnlocked");
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        super.save(compoundNBT);

        ItemStackHelper.saveAllItems(compoundNBT, this.items);
        compoundNBT.putString("correctPassword", correctPassword);
        compoundNBT.putBoolean("isUnlocked", isUnlocked);
        return compoundNBT;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tags = this.getUpdateTag();
        ItemStackHelper.saveAllItems(tags, this.items);
        return new SUpdateTileEntityPacket(this.worldPosition, 1, tags);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags = super.getUpdateTag();
        tags.putString("correctPassword", correctPassword);
        tags.putBoolean("isUnlocked", isUnlocked);
        return tags;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("Safe Lock");
    }


    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        if(isUnlocked)
        {
            isUnlocked = false;
            Messages.sendToPlayer((ServerPlayerEntity) playerInventory.player, isUnlocked, correctPassword);
            return new ContainerSafe(id, playerInventory, this, this.fields);
        }
        else
            return new ContainerSafeLocked(id, playerInventory, this, this.fields);
    }

    private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 1:
                    return getBlockPos().getX();
                case 2:
                    return getBlockPos().getY();
                case 3:
                    return getBlockPos().getZ();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 1:
                    fields.set(1,getBlockPos().getX());
                case 2:
                    fields.set(2,getBlockPos().getY());
                case 3:
                    fields.set(3,getBlockPos().getZ());
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    void encodeExtraData(PacketBuffer buffer) {
        buffer.writeByte(fields.getCount());
        buffer.writeInt(getBlockPos().getX());
        buffer.writeInt(getBlockPos().getY());
        buffer.writeInt(getBlockPos().getZ());
    }

    @Override
    public int[] getSlotsForFace(Direction p_180463_1_) {
        return new int[27];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        int size = getContainerSize();
        for(int i = 0; i < size; i++)
        {
            if(!getItem(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStackHelper.removeItem(items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ()) <= 64;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == Direction.UP) {
                return this.handlers[0].cast();
            } else if (side == Direction.DOWN) {
                return this.handlers[1].cast();
            } else {
                return this.handlers[2].cast();
            }
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
            handler.invalidate();
        }
    }
}
