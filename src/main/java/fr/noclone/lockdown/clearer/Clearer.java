package fr.noclone.lockdown.clearer;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Clearer extends Block {

    public Clearer() {
        super(Properties.of(Material.HEAVY_METAL).strength(5).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops());
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
        return new TileEntityClearer();
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
        if (tileEntity instanceof TileEntityClearer && player instanceof ServerPlayerEntity) {
            TileEntityClearer te = (TileEntityClearer) tileEntity;
            NetworkHooks.openGui((ServerPlayerEntity) player, te, te::encodeExtraData);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        TileEntity te = event.getWorld().getBlockEntity(event.getPos());
        if(te instanceof TileEntityClearer)
        {
            TileEntityClearer tileEntityClearer = (TileEntityClearer) te;
            if(tileEntityClearer.getOwner() != null && !tileEntityClearer.getOwner().equals(Minecraft.getInstance().player.getUUID()))
                    event.setCanceled(true);
            InventoryHelper.dropContents(te.getLevel(),te.getBlockPos(), tileEntityClearer);
        }
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(p_220082_1_, world, pos, p_220082_4_, p_220082_5_);
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityClearer) {
            TileEntityClearer te = (TileEntityClearer) tileEntity;
            if(te.getOwner() == null)
            {
                te.setOwner(Minecraft.getInstance().player.getUUID());
                te.getLevel().sendBlockUpdated(te.getBlockPos(), te.getBlockState(), te.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }
}