package fr.noclone.lockdown.Safe;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncSafe;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class SafeScreen extends ContainerScreen<ContainerSafe> {

    public static final ResourceLocation UNLOCKED = new ResourceLocation(LockDown.MODID, "textures/gui/safe.png");

    TileEntitySafe tileEntitySafe;

    public SafeScreen(ContainerSafe containerSafe, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerSafe, playerInventory, textComponent);

        IIntArray fields = containerSafe.getFields();
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntitySafe)
            tileEntitySafe = (TileEntitySafe) te;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        this.renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        if(minecraft == null)
            return;
        RenderSystem.color4f(1,1,1,1);

        minecraft.getTextureManager().bind(UNLOCKED);


        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;

        blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void onClose() {
        tileEntitySafe.setUnlocked(false);
        Messages.INSTANCE.sendToServer(new PacketSyncSafe(false, tileEntitySafe.getCorrectPassword(), tileEntitySafe.getOwner()));
        BlockState state = minecraft.level.getBlockState(tileEntitySafe.getBlockPos());
        minecraft.level.setBlock(tileEntitySafe.getBlockPos(), state.setValue(BlockStateProperties.LOCKED, true),
                Constants.BlockFlags.NOTIFY_NEIGHBORS+Constants.BlockFlags.BLOCK_UPDATE);
        super.onClose();
    }
}
