package fr.noclone.lockdown.shop;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Shop extends Block {

    public Shop() {
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
        return new TileEntityShop();
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
        if (tileEntity instanceof TileEntityShop && player instanceof ServerPlayerEntity) {
            TileEntityShop te = (TileEntityShop) tileEntity;
            NetworkHooks.openGui((ServerPlayerEntity) player, te, te::encodeExtraData);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        TileEntity te = event.getWorld().getBlockEntity(event.getPos());
        if(te instanceof TileEntityShop)
        {
            TileEntityShop tileentityshop = (TileEntityShop) te;
            if(tileentityshop.getOwner() != null && !tileentityshop.getOwner().equals(event.getPlayer().getUUID()))
                    event.setCanceled(true);
            InventoryHelper.dropItemStack(te.getLevel(),te.getBlockPos().getX(), te.getBlockPos().getY(),te.getBlockPos().getZ(), tileentityshop.getItem(0));
        }
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(p_220082_1_, world, pos, p_220082_4_, p_220082_5_);
    }

    @Override
    public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        super.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);

        TileEntity tileEntity = p_180633_1_.getBlockEntity(p_180633_2_);
        if (tileEntity instanceof TileEntityShop) {
            TileEntityShop te = (TileEntityShop) tileEntity;
            if(te.getOwner() == null)
            {
                te.setOwner(p_180633_4_.getUUID());
                te.getLevel().sendBlockUpdated(te.getBlockPos(), te.getBlockState(), te.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if(stack.hasTag() && stack.getTag().contains("price"))
        {
            event.getToolTip().add(new TranslationTextComponent(TextFormatting.GOLD+""+stack.getTag().getInt("price")+"$"));
        }
    }
}
