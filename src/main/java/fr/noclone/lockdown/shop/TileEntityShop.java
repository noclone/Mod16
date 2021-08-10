package fr.noclone.lockdown.shop;

import fr.noclone.lockdown.Safe.TileEntitySafe;
import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
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

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityShop extends LockableTileEntity implements ISidedInventory, ITickableTileEntity{

    private NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers;


    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    private UUID owner;

    private BlockPos OwnerAccountPos;

    private int OwnerAccountNumber;

    public void setAdminMode(boolean adminMode) {
        AdminMode = adminMode;
    }

    private boolean AdminMode = false;

    public TileEntityShop() {
        super(ModTileEntities.SHOP_TILE_ENTITY.get());
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);

        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundNBT, this.items);
        owner = compoundNBT.getUUID("owner");
        if(compoundNBT.contains("OwnerAccountX"))
        {
            OwnerAccountPos = new BlockPos(compoundNBT.getInt("OwnerAccountX"),compoundNBT.getInt("OwnerAccountY"),compoundNBT.getInt("OwnerAccountZ"));
            OwnerAccountNumber = compoundNBT.getInt("OwnerAccountNum");
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        super.save(compoundNBT);

        ItemStackHelper.saveAllItems(compoundNBT, this.items);
        compoundNBT.putUUID("owner", owner);
        if(OwnerAccountPos != null)
        {
            compoundNBT.putInt("OwnerAccountX", OwnerAccountPos.getX());
            compoundNBT.putInt("OwnerAccountY", OwnerAccountPos.getY());
            compoundNBT.putInt("OwnerAccountZ", OwnerAccountPos.getZ());
            compoundNBT.putInt("OwnerAccountNum", OwnerAccountNumber);
        }
        return compoundNBT;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundNBT tag = pkt.getTag();
        owner = tag.getUUID("owner");
        if(tag.contains("OwnerAccountX"))
        {
            OwnerAccountPos = new BlockPos(tag.getInt("OwnerAccountX"),tag.getInt("OwnerAccountY"),tag.getInt("OwnerAccountZ"));
            OwnerAccountNumber = tag.getInt("OwnerAccountNum");
        }
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
        if(OwnerAccountPos != null)
        {
            tags.putInt("OwnerAccountX", OwnerAccountPos.getX());
            tags.putInt("OwnerAccountY", OwnerAccountPos.getY());
            tags.putInt("OwnerAccountZ", OwnerAccountPos.getZ());
            tags.putInt("OwnerAccountNum", OwnerAccountNumber);
        }
        return tags;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("Shop");
    }


    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new ContainerShop(id, playerInventory, this, this.fields);
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
        return new int[28];
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
        return 28;
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
        if(!level.isClientSide) {
            if(makeLink && !items.get(0).isEmpty())
            {
                makeLink = false;
                MakeLink();
            }
        }
    }

    public void ChangeGhost(int index, boolean place, ItemStack stack) {
        if(place)
        {
            ItemStack ghost = stack.copy();
            ghost.setCount(1);
            if(!ghost.hasTag())
                ghost.setTag(new CompoundNBT());
            ghost.getTag().putInt("price", 1);
            items.set(index, ghost);
        }
        else
        {
            items.set(index, ItemStack.EMPTY);
        }
    }

    public void PriceChanged(int index, int value) {

        items.get(index).getTag().putInt("price", value);
    }

    public void BuyItem(int index, boolean shift) {
        if(items.get(0).isEmpty() || OwnerAccountPos == null)
            return;

        TileEntity tile = level.getBlockEntity(OwnerAccountPos);
        if(!(tile instanceof TileEntityBankServer))
            return;
        TileEntityBankServer ownerServer = (TileEntityBankServer) tile;
        ItemStack ownerCard = ownerServer.getCards().get(OwnerAccountNumber);
        if(ownerCard.isEmpty())
            return;

        ItemStack card = ItemStack.EMPTY;
        TileEntityBankServer server = (TileEntityBankServer) level.getBlockEntity(new BlockPos(items.get(0).getTag().getInt("serverX"),items.get(0).getTag().getInt("serverY"),items.get(0).getTag().getInt("serverZ")));

        for(int i = 0; i < TileEntityBankServer.MAX_CARDS; i++)
        {
            if(!server.getCards().get(i).isEmpty() && items.get(0).getTag().getUUID("id").equals(server.getCards().get(i).getTag().getUUID("id")))
                card = server.getCards().get(i);
        }
        if(card.isEmpty())
            return;

        ItemStack product = items.get(index).copy();
        int price = product.getTag().getInt("price");

        if(card.getTag().getInt("balance") < price*(shift ? 64 : 1))
            return;

        TileEntity te = level.getBlockEntity(new BlockPos(getBlockPos().above()));
        if(!(te instanceof TileEntitySafe))
            return;
        TileEntitySafe safe = (TileEntitySafe)te;
        int i = 0;
        int total = shift ? 64 : 1;
        while(i < safe.getItems().size() && total > 0)
        {
            ItemStack item;
            if(AdminMode)
            {
                item = product.copy();
                item.getTag().remove("price");
                item.setCount(64);
            }
            else
                item = safe.getItems().get(i);

            if(!item.isEmpty() && item.getItem().getRegistryType().equals(product.getItem().getRegistryType())) {

                int wanted = total == 1 ? 1 : item.getCount();
                item.setCount(item.getCount() - wanted);
                ItemStack stackToGive = product.copy();
                stackToGive.getTag().remove("price");
                if (stackToGive.getTag().isEmpty())
                    stackToGive.setTag(null);
                stackToGive.setCount(wanted);
                level.getPlayerByUUID(Minecraft.getInstance().player.getUUID()).inventory.add(stackToGive);
                card.getTag().putInt("balance", card.getTag().getInt("balance") - price * wanted);
                ownerCard.getTag().putInt("balance", ownerCard.getTag().getInt("balance") + price * wanted);
                total -= wanted;
            }
            i++;
        }

    }

    boolean makeLink;

    public void LinkOwnerCard() {
        makeLink = true;
    }

    private void MakeLink()
    {
        ItemStack card = items.get(0);
        OwnerAccountPos = new BlockPos(card.getTag().getInt("serverX"),card.getTag().getInt("serverY"),card.getTag().getInt("serverZ"));
        TileEntityBankServer server = (TileEntityBankServer) level.getBlockEntity(OwnerAccountPos);
        for(int i = 0; i < TileEntityBankServer.MAX_CARDS; i++)
        {
            if(server.getCards().get(i).getTag().getUUID("id").equals(card.getTag().getUUID("id")))
            {
                OwnerAccountNumber = i;
                break;
            }
        }
    }
}