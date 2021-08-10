package fr.noclone.lockdown.bankserver;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketCopyCard;
import fr.noclone.lockdown.network.PacketDeleteCard;
import fr.noclone.lockdown.network.PacketLinkCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

import static fr.noclone.lockdown.utils.Utils.formatInt;


public class BankServerScreen extends ContainerScreen<ContainerBankServer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LockDown.MODID, "textures/gui/bank_server.png");

    TileEntityBankServer tileEntityBankServer;

    ContainerBankServer containerBankServer;

    List<Button> buttonList;

    public BankServerScreen(ContainerBankServer containerBankServer, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerBankServer, playerInventory, textComponent);

        this.containerBankServer = containerBankServer;

        IIntArray fields = containerBankServer.getFields();
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntityBankServer)
            tileEntityBankServer = (TileEntityBankServer) te;
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        addButton(new Button(getGuiLeft()+114,getGuiTop()+31, 55, 18, new TranslationTextComponent("Link Card")
                , (button)->{LinkCard();}));

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;

        buttonList = new ArrayList<>();

        buttonList.add(new Button(getGuiLeft()+100,posY+20+10, 8, 8, new TranslationTextComponent("D"), (button)->{DeleteCard(0);}));
        buttonList.add(new Button(getGuiLeft()+100,posY+20+20, 8, 8, new TranslationTextComponent("D"), (button)->{DeleteCard(1);}));
        buttonList.add(new Button(getGuiLeft()+100,posY+20+30, 8, 8, new TranslationTextComponent("D"), (button)->{DeleteCard(2);}));
        buttonList.get(0).visible = false;
        buttonList.get(1).visible = false;
        buttonList.get(2).visible = false;
        addButton(buttonList.get(0));
        addButton(buttonList.get(1));
        addButton(buttonList.get(2));

        buttonList.add(new Button(getGuiLeft()+90,posY+20+10, 8, 8, new TranslationTextComponent("C"), (button)->{CopyCard(0);}));
        buttonList.add(new Button(getGuiLeft()+90,posY+20+20, 8, 8, new TranslationTextComponent("C"), (button)->{CopyCard(1);}));
        buttonList.add(new Button(getGuiLeft()+90,posY+20+30, 8, 8, new TranslationTextComponent("C"), (button)->{CopyCard(2);}));
        buttonList.get(3).visible = false;
        buttonList.get(4).visible = false;
        buttonList.get(5).visible = false;
        addButton(buttonList.get(3));
        addButton(buttonList.get(4));
        addButton(buttonList.get(5));
    }

    private void CopyCard(int i) {
        Messages.INSTANCE.sendToServer(new PacketCopyCard(tileEntityBankServer.getBlockPos(),i));
    }

    private void DeleteCard(int i) {
        Messages.INSTANCE.sendToServer(new PacketDeleteCard(tileEntityBankServer.getBlockPos(),i));
    }

    private void LinkCard()
    {
        Messages.INSTANCE.sendToServer(new PacketLinkCard(tileEntityBankServer.getBlockPos()));
    }


    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        for(int i = 0; i < TileEntityBankServer.MAX_CARDS; i++)
        {
            if(!tileEntityBankServer.getCards().get(i).isEmpty())
            {
                ItemStack card = tileEntityBankServer.getCards().get(i);
                CompoundNBT tag = card.getTag();
                font.draw(matrixStack,TextFormatting.GRAY+"Acc "+i+": "
                        +TextFormatting.DARK_GREEN+tileEntityBankServer.getLevel().getPlayerByUUID(tag.getUUID("owner")).getScoreboardName()+" "
                        +TextFormatting.GOLD+formatInt(tag.getInt("balance"))+" $", posX + 5,posY+20+10*(i+1), 0xFFFFFF);
                if(tileEntityBankServer.getOwner().equals(Minecraft.getInstance().player.getUUID()))
                {
                    buttonList.get(i).visible = true;
                    buttonList.get(i+3).visible = true;
                }
            }
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
