package fr.noclone.lockdown.Safe;

import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncSafe;
import fr.noclone.lockdown.network.PacketSyncSafeClient;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import org.lwjgl.system.windows.MSG;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class BlockSafe extends Block {


    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public BlockSafe() {
        super(AbstractBlock.Properties.of(Material.HEAVY_METAL).strength(5).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops());
        this.registerDefaultState(this.getStateDefinition().any().setValue(LOCKED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.LOCKED);
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
        return new TileEntitySafe();
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
        if (tileEntity instanceof TileEntitySafe && player instanceof ServerPlayerEntity) {
            TileEntitySafe te = (TileEntitySafe) tileEntity;
            NetworkHooks.openGui((ServerPlayerEntity) player, te, te::encodeExtraData);
            Messages.INSTANCE.sendTo(new PacketSyncSafeClient(te.isUnlocked(), te.getCorrectPassword(), te.getOwner()), ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        TileEntity te = event.getWorld().getBlockEntity(event.getPos());
        if(te instanceof TileEntitySafe)
        {
            TileEntitySafe tileEntitySafe = (TileEntitySafe) te;
            if(tileEntitySafe.getOwner() != null && !tileEntitySafe.getOwner().equals(Minecraft.getInstance().player.getUUID()))
                event.setCanceled(true);
        }
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(p_220082_1_, world, pos, p_220082_4_, p_220082_5_);
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntitySafe) {
            TileEntitySafe te = (TileEntitySafe) tileEntity;
            if(te.getOwner() == null)
            {
                te.setOwner(Minecraft.getInstance().player.getUUID());
            }
        }
    }
}
