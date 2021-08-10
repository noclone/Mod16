package fr.noclone.lockdown.bankserver;

import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.init.ModTileEntities;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncBankServer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TileEntityBankServer extends LockableTileEntity implements ISidedInventory, ITickableTileEntity{

    private NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers;


    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    private UUID owner;

    public NonNullList<ItemStack> getCards() {
        return cards;
    }

    public void setCards(NonNullList<ItemStack> cards) {
        this.cards = cards;
    }

    private NonNullList<ItemStack> cards;

    public static int MAX_CARDS = 3;

    public TileEntityBankServer() {
        super(ModTileEntities.BANK_SERVER_TILE_ENTITY.get());
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        this.cards = NonNullList.withSize(MAX_CARDS,ItemStack.EMPTY);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);

        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundNBT, this.items);
        this.cards = NonNullList.withSize(MAX_CARDS, ItemStack.EMPTY);
        loadCards(compoundNBT, this.cards);

        owner = compoundNBT.getUUID("owner");
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        super.save(compoundNBT);

        ItemStackHelper.saveAllItems(compoundNBT, this.items);
        saveCards(compoundNBT, this.cards);
        compoundNBT.putUUID("owner", owner);
        return compoundNBT;
    }

    public CompoundNBT saveCards(CompoundNBT compoundNBT, NonNullList<ItemStack> list) {
        ListNBT listnbt = new ListNBT();

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Slot", (byte)i);
                itemstack.save(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }
        compoundNBT.put("Cards", listnbt);

        return compoundNBT;
    }

    public void loadCards(CompoundNBT p_191283_0_, NonNullList<ItemStack> p_191283_1_) {
        ListNBT listnbt = p_191283_0_.getList("Cards", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j >= 0 && j < p_191283_1_.size()) {
                p_191283_1_.set(j, ItemStack.of(compoundnbt));
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundNBT tag = pkt.getTag();
        owner = tag.getUUID("owner");
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.cards = NonNullList.withSize(MAX_CARDS, ItemStack.EMPTY);
        loadCards(tag, this.cards);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tags = this.getUpdateTag();
        ItemStackHelper.saveAllItems(tags, this.items);
        saveCards(tags, this.cards);
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
        return new TranslationTextComponent("Bank Server");
    }


    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new ContainerBankServer(id, playerInventory, this, this.fields);
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
        return new int[2];
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
            for(int i = 0; i < 3; i++)
            {
                ItemStack card = cards.get(i);
                if(!card.isEmpty())
                {
                    if(card.hasTag() && card.getTag().contains("balance"))
                    {
                        card.getTag().putInt("balance", card.getTag().getInt("balance")+1);
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }
            }
        }
    }

    private boolean isCardsFull()
    {
        for(int i = 0; i < MAX_CARDS; i++)
        {
            if(cards.get(i).isEmpty())
                return false;
        }
        return true;
    }

    public void DeleteCard(int i)
    {
        cards.set(i, ItemStack.EMPTY);
    }

    public void LinkCard() {
        if(isCardsFull() || !owner.equals(Minecraft.getInstance().player.getUUID()))
            return;
        ItemStack item = items.get(0).copy();
        if(item.getItem() instanceof CreditCard)
        {
            if(!item.hasTag())
            {
                item.setTag(new CompoundNBT());
                item.getTag().putUUID("banker", Minecraft.getInstance().player.getUUID());
            }
            if(!item.getTag().contains("owner"))
            {
                item.getTag().putUUID("owner",Minecraft.getInstance().player.getUUID());
                item.getTag().putInt("balance",0);
                item.getTag().putInt("serverX", getBlockPos().getX());
                item.getTag().putInt("serverY", getBlockPos().getY());
                item.getTag().putInt("serverZ", getBlockPos().getZ());

                int i = 0;
                while(!cards.get(i).isEmpty())
                    i++;
                cards.set(i, item.copy());

                items.set(1,item.copy());
                items.set(0,ItemStack.EMPTY);
                ItemStackHelper.saveAllItems(getUpdateTag(), items);
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }
}