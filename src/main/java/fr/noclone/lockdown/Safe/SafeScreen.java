package fr.noclone.lockdown.Safe;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class SafeScreen extends ContainerScreen<ContainerSafe> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LockDown.MODID, "textures/gui/safe.png");

    public SafeScreen(ContainerSafe containerSafe, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerSafe, playerInventory, textComponent);
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        buttons.add(0, new Button(getGuiLeft(),getGuiTop(),20,20,new TranslationTextComponent("0"),
                (button) -> {getMinecraft().player.closeContainer();}));
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
        minecraft.getTextureManager().bind(TEXTURE);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;

        blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void onNumberClicked(int nb)
    {

    }
}
