package fr.noclone.lockdown.Safe;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class SafeScreenLocked extends ContainerScreen<ContainerSafeLocked> {
    public static final ResourceLocation LOCKED = new ResourceLocation(LockDown.MODID, "textures/gui/safe_lock.png");

    ContainerSafeLocked containerSafeLocked;

    TileEntitySafe tileEntitySafe;

    String password = "";

    public SafeScreenLocked(ContainerSafeLocked containerSafeLocked, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerSafeLocked, playerInventory, textComponent);

        this.containerSafeLocked = containerSafeLocked;

        IIntArray fields = containerSafeLocked.getFields();

        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntitySafe)
            tileEntitySafe = (TileEntitySafe) te;
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        addButton(new Button(getGuiLeft()+63,getGuiTop()+69,18,18,new TranslationTextComponent("1"),
                (button)->{onNumberClicked(1);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69,18,18,new TranslationTextComponent("2"),
                (button)->{onNumberClicked(2);}));
        addButton(new Button(getGuiLeft()+63+18*2,getGuiTop()+69,18,18,new TranslationTextComponent("3"),
                (button)->{onNumberClicked(3);}));
        addButton(new Button(getGuiLeft()+63,getGuiTop()+69+18,18,18,new TranslationTextComponent("4"),
                (button)->{onNumberClicked(4);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69+18,18,18,new TranslationTextComponent("5"),
                (button)->{onNumberClicked(5);}));
        addButton(new Button(getGuiLeft()+63+18*2,getGuiTop()+69+18,18,18,new TranslationTextComponent("6"),
                (button)->{onNumberClicked(6);}));
        addButton(new Button(getGuiLeft()+63,getGuiTop()+69+18*2,18,18,new TranslationTextComponent("7"),
                (button)->{onNumberClicked(7);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69+18*2,18,18,new TranslationTextComponent("8"),
                (button)->{onNumberClicked(8);}));
        addButton(new Button(getGuiLeft()+63+18*2,getGuiTop()+69+18*2,18,18,new TranslationTextComponent("9"),
                (button)->{onNumberClicked(9);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69+18*3,18,18,new TranslationTextComponent("0"),
                (button)->{onNumberClicked(0);}));

    }

    @Override
    protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
        this.font.draw(p_230451_1_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        this.renderTooltip(matrixStack, x, y);
        drawCenteredString(matrixStack, font, password+"   "+tileEntitySafe.isUnlocked(), getGuiLeft()+width/2, getGuiTop()+5, 0x52FF33);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        if(minecraft == null)
            return;
        RenderSystem.color4f(1,1,1,1);

        minecraft.getTextureManager().bind(LOCKED);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;

        blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void onNumberClicked(int nb)
    {
        password += nb;
        if(password.equals(tileEntitySafe.getCorrectPassword()))
        {
            tileEntitySafe.setUnlocked(true);
        }
    }
}
