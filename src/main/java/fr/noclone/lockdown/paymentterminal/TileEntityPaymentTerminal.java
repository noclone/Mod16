package fr.noclone.lockdown.paymentterminal;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityPaymentTerminal extends LockableTileEntity implements ISidedInventory, ITickableTileEntity{

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers;


    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    private UUID owner;

    public TileEntityPaymentTerminal() {
        super(ModTileEntities.PAYMENT_TERMINAL_TILE_ENTITY.get());
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);

        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundNBT, this.items);
        owner = compoundNBT.getUUID("owner");
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        super.save(compoundNBT);

        ItemStackHelper.saveAllItems(compoundNBT, this.items);
        compoundNBT.putUUID("owner", owner);
        return compoundNBT;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundNBT tag = pkt.getTag();
        owner = tag.getUUID("owner");
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
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
        tags.putUUID("owner", owner);
        return tags;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("Payment Terminal");
    }


    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new ContainerPaymentTerminal(id, playerInventory, this, this.fields);
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
        buffer.writeUUID(owner);
        buffer.writeByte(fields.getCount());
        buffer.writeInt(getBlockPos().getX());
        buffer.writeInt(getBlockPos().getY());
        buffer.writeInt(getBlockPos().getZ());
    }

    @Override
    public int[] getSlotsForFace(Direction p_180463_1_) {
        return new int[getContainerSize()];
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
        return 2;
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

    @Override
    public void tick() {
        if(!level.isClientSide)
        {
        }
    }

    public void Pay(int amount, PlayerEntity playerEntity) {
        if(items.get(0).isEmpty() || items.get(1).isEmpty())
            return;
        CompoundNBT Stag = items.get(0).getTag();
        CompoundNBT Rtag = items.get(1).getTag();

        if(!Stag.getUUID("owner").equals(playerEntity.getUUID()))
            return;

        TileEntityBankServer Tsender = (TileEntityBankServer) level.getBlockEntity(new BlockPos(Stag.getInt("serverX"),Stag.getInt("serverY"),Stag.getInt("serverZ")));
        TileEntityBankServer Treceiver = (TileEntityBankServer) level.getBlockEntity(new BlockPos(Rtag.getInt("serverX"),Rtag.getInt("serverY"),Rtag.getInt("serverZ")));

        ItemStack sender = ItemStack.EMPTY;
        ItemStack receiver = ItemStack.EMPTY;

        for(int i = 0; i < TileEntityBankServer.MAX_CARDS; i++)
        {
            if(!Tsender.getCards().get(i).isEmpty() && Tsender.getCards().get(i).getTag().getUUID("id").equals(Stag.getUUID("id")))
                sender = Tsender.getCards().get(i);
            if(!Treceiver.getCards().get(i).isEmpty() && Treceiver.getCards().get(i).getTag().getUUID("id").equals(Rtag.getUUID("id")))
                receiver = Treceiver.getCards().get(i);
        }

        if(sender.isEmpty() || receiver.isEmpty())
            return;

        CompoundNBT SCtag = sender.getTag();
        CompoundNBT RCtag = receiver.getTag();

        if(SCtag.getInt("balance") < amount)
            return;

        SCtag.putInt("balance", SCtag.getInt("balance")-amount);
        RCtag.putInt("balance", RCtag.getInt("balance")+amount);
    }
}