package fr.noclone.lockdown.clearer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.creditcard.CreditCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClearerScreen extends ContainerScreen<ContainerClearer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LockDown.MODID, "textures/gui/clearer.png");

    TileEntityClearer tileEntityClearer;

    ContainerClearer containerClearer;


    public ClearerScreen(ContainerClearer containerClearer, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerClearer, playerInventory, textComponent);

        this.containerClearer = containerClearer;

        IIntArray fields = containerClearer.getFields();
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntityClearer)
            tileEntityClearer = (TileEntityClearer) te;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        this.renderTooltip(matrixStack, x, y);

        if(!containerClearer.getSlot(0).getItem().isEmpty() && containerClearer.getSlot(0).getItem().getItem() instanceof CreditCard)
        {
            if(!containerClearer.getSlot(0).getItem().hasTag() && containerClearer.getSlot(0).getItem().getTag().getUUID("banker").equals(Minecraft.getInstance().player.getUUID()))
                drawCenteredString(matrixStack,font,new TranslationTextComponent("Only the banker can clear a credit card !"), getGuiLeft()+imageWidth/2, getGuiTop()+20, 0xFF0000);
            else
                drawCenteredString(matrixStack,font,new TranslationTextComponent("Credit Card Cleared !"), getGuiLeft()+imageWidth/2, getGuiTop()+20, 0x52FF33);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        if(minecraft == null)
            return;
        RenderSystem.color4f(1,1,1,1);

        minecraft.getTextureManager().bind(TEXTURE);


        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;

        blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
