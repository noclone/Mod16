package fr.noclone.lockdown.paymentterminal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketPayInTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;


public class PaymentTerminalScreen extends ContainerScreen<ContainerPaymentTerminal> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LockDown.MODID, "textures/gui/payment_terminal.png");

    TileEntityPaymentTerminal tileEntityPaymentTerminal;

    ContainerPaymentTerminal containerPaymentTerminal;

    TextFieldWidget box;

    int message = 0;

    public PaymentTerminalScreen(ContainerPaymentTerminal containerPaymentTerminal, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerPaymentTerminal, playerInventory, textComponent);

        this.containerPaymentTerminal = containerPaymentTerminal;

        IIntArray fields = PaymentTerminalScreen.this.containerPaymentTerminal.getFields();
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntityPaymentTerminal)
            tileEntityPaymentTerminal = (TileEntityPaymentTerminal) te;
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        addButton(new Button(getGuiLeft()+60,getGuiTop()+50, 47, 10, new TranslationTextComponent("Pay")
                , (button)->{Pay();}));

        int textfieldW = 47;
        int textfieldH = 10;
        box = new TextFieldWidget(font,getGuiLeft()+60, getGuiTop()+19, textfieldW, textfieldH,new TranslationTextComponent(""));
        box.setVisible(true);
        addWidget(box);
    }

    private void Pay() {
        boolean valid = true;
        for(int i = 0; i < box.getValue().length(); i++)
        {
            if(!(box.getValue().charAt(i) >= '0' && box.getValue().charAt(i) <= '9'))
            {
                valid = false;
                break;
            }
        }
        if(box.getValue().isEmpty() || !valid)
        {
            message = 1;
            return;
        }
        else if(!containerPaymentTerminal.getSlot(0).getItem().getTag().getUUID("owner").equals(Minecraft.getInstance().player.getUUID()))
            message = 2;
        else if(containerPaymentTerminal.getSlot(0).getItem().getTag().getInt("balance") < Integer.parseInt(box.getValue()))
            message = 3;
        else
            message = 4;


        Messages.INSTANCE.sendToServer(new PacketPayInTerminal(tileEntityPaymentTerminal.getBlockPos(), Integer.parseInt(box.getValue())));
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);

        if(box != null)
            box.render(matrixStack, x, y, partialTicks);
        switch (message)
        {
            case 1:
                drawCenteredString(matrixStack,font,new TranslationTextComponent(TextFormatting.RED+"Please enter an amount !"), getGuiLeft()+imageWidth/2, getGuiTop()+63, 0xFFFFFF);
                break;
            case 2:
                drawCenteredString(matrixStack,font,new TranslationTextComponent(TextFormatting.RED+"Thief ! Pay with your own card !"), getGuiLeft()+imageWidth/2, getGuiTop()+63, 0xFFFFFF);
                break;
            case 3:
                drawCenteredString(matrixStack,font,new TranslationTextComponent(TextFormatting.RED+"Not enough money !"), getGuiLeft()+imageWidth/2, getGuiTop()+63, 0xFFFFFF);
                break;
            case 4:
                drawCenteredString(matrixStack,font,new TranslationTextComponent(TextFormatting.GREEN+"Money transfered !"), getGuiLeft()+imageWidth/2, getGuiTop()+63, 0xFFFFFF);
                break;
            default:
                break;
        }
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
}
