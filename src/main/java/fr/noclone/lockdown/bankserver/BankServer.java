package fr.noclone.lockdown.bankserver;

import fr.noclone.lockdown.init.ModItems;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncBankServer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class BankServer extends Block {

    public BankServer() {
        super(AbstractBlock.Properties.of(Material.HEAVY_METAL).strength(5).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getStateDefinition().any().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityBankServer();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        this.interactWith(world, pos, player);
        return ActionResultType.CONSUME;
    }

    public void interactWith(World world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityBankServer && player instanceof ServerPlayerEntity) {
            TileEntityBankServer te = (TileEntityBankServer) tileEntity;
            NetworkHooks.openGui((ServerPlayerEntity) player, te, te::encodeExtraData);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        TileEntity te = event.getWorld().getBlockEntity(event.getPos());
        if(te instanceof TileEntityBankServer)
        {
            TileEntityBankServer tileEntityBankServer = (TileEntityBankServer) te;
            if(tileEntityBankServer.getOwner() != null && !tileEntityBankServer.getOwner().equals(event.getPlayer().getUUID()))
                    event.setCanceled(true);
            InventoryHelper.dropContents(te.getLevel(),te.getBlockPos(), tileEntityBankServer);
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState p_180633_3_, @Nullable LivingEntity entity, ItemStack p_180633_5_) {
        super.setPlacedBy(world, pos, p_180633_3_, entity, p_180633_5_);

        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityBankServer) {
            TileEntityBankServer te = (TileEntityBankServer) tileEntity;
            if(te.getOwner() == null) {
                te.setOwner(entity.getUUID());
                te.getLevel().sendBlockUpdated(te.getBlockPos(), te.getBlockState(), te.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }
}
