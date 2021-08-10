package fr.noclone.lockdown.shop;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.clearer.ContainerClearer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ShopScreen extends ContainerScreen<ContainerShop> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LockDown.MODID, "textures/gui/shop.png");
    public static final ResourceLocation SELECTEDSLOT = new ResourceLocation(LockDown.MODID, "textures/gui/selectedslot.png");

    TileEntityShop tileEntityShop;

    ContainerShop containerShop;

    TextFieldWidget box;

    int xPos = -1;
    int yPos = -1;

    Slot selectedSlot;




    List<Button> buttonList = new ArrayList<>();

    public ShopScreen(ContainerShop containerShop, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerShop, playerInventory, textComponent);

        this.containerShop = containerShop;

        IIntArray fields = containerShop.getFields();
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntityShop)
            tileEntityShop = (TileEntityShop) te;

        imageHeight = 205;
    }

    @Override
    protected void init() {
        super.init();

        buttonList.add(new Button(getGuiLeft() + 8, getGuiTop() + 21, 18, 18, new TranslationTextComponent("-")
                , (button) -> { PlusMinus(-1); }));
        buttonList.add(new Button(getGuiLeft() + 72, getGuiTop() + 21, 18, 18, new TranslationTextComponent("+")
                , (button) -> { PlusMinus(1); }));
        buttons.clear();
        addButton(buttonList.get(0));
        addButton(buttonList.get(1));
        int textfieldW = 44;
        int textfieldH = 18;
        box = new TextFieldWidget(font, getGuiLeft() + 27, getGuiTop() + 21, textfieldW, textfieldH, new TranslationTextComponent(""));
        box.setVisible(false);
        box.setValue("0");
        box.setMaxLength(9);
        addWidget(box);


        buttonList.add(new Button(getGuiLeft() + 95, getGuiTop() + 21, 30, 18, new TranslationTextComponent("Set")
                , (button) -> { SetPrice(); }));
        addButton(buttonList.get(2));

        buttonList.add(new Button(getGuiLeft()-19, getGuiTop(), 18, 18, new TranslationTextComponent("O")
                , (button) -> { SwapOwnerMode(); }));
        addButton(buttonList.get(3));

        buttonList.add(new Button(getGuiLeft()-19, getGuiTop() + 19, 18, 18, new TranslationTextComponent("I")
                , (button) -> { SwapInfiniteMode(); }));

        addButton(buttonList.get(4));

        buttonList.get(0).visible = false;
        buttonList.get(1).visible = false;
        buttonList.get(2).visible = false;
        buttonList.get(3).visible = false;
        buttonList.get(4).visible = false;

    }

    private void SwapInfiniteMode() {
        containerShop.AdminMode = !containerShop.AdminMode;
        Messages.INSTANCE.sendToServer(new PacketShopAdminMode(tileEntityShop.getBlockPos(), containerShop.AdminMode));
    }

    private void SwapOwnerMode() {
        if(containerShop.OwnerMode)
        {
            containerShop.OwnerMode = false;
            for (int i = 0; i < 3; i++) {
                buttonList.get(i).visible = false;
            }
            box.visible = false;
        }
        else {
            containerShop.OwnerMode = true;
            for (int i = 0; i < 3; i++) {
                buttonList.get(i).visible = true;
            }
            box.visible = true;
        }
    }


    private void PlusMinus(int add) {
        if(box.getValue().isEmpty() || !IsValid())
            return;
        int val = Integer.parseInt(box.getValue());
        box.setValue(val+add+"");
    }

    private boolean IsValid()
    {
        boolean valid = true;
        for(int i = 0; i < box.getValue().length(); i++)
        {
            if(!(box.getValue().charAt(i) >= '0' && box.getValue().charAt(i) <= '9'))
            {
                valid = false;
                break;
            }
        }
        return valid;
    }

    @Override
    protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
        this.font.draw(p_230451_1_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        if(tileEntityShop.getOwner().equals(Minecraft.getInstance().player.getUUID()) && !buttonList.get(3).visible)
            buttonList.get(3).visible = true;

        if(Minecraft.getInstance().player.isCreative() && !buttonList.get(4).visible)
            buttonList.get(4).visible = true;

        if(box != null)
            box.render(matrixStack, x, y, partialTicks);
        if(containerShop.displayLinked && containerShop.time < 100)
        {
            drawCenteredString(matrixStack,font, TextFormatting.GREEN+"Account Linked !",  getGuiLeft()+containerShop.getSlot(0).x-10, getGuiTop()+containerShop.getSlot(0).y + 25, 0xFFFFFF);
            containerShop.time+=1;
        }

        this.renderTooltip(matrixStack, x, y);
    }

    private void SetPrice() {
        if(selectedSlot == null || !IsValid())
            return;
        Messages.INSTANCE.sendToServer(new PacketPriceChanged(tileEntityShop.getBlockPos(), selectedSlot.index, Integer.parseInt(box.getValue())));
    }

    private void drawSelectedSlot(MatrixStack matrixStack) {
        if(xPos == -1)
            return;

        minecraft.getTextureManager().bind(SELECTEDSLOT);
        blit(matrixStack, getGuiLeft()+xPos-1, getGuiTop()+yPos-1, 0, 0, 256, 256);
    }

    @Override
    protected void slotClicked(Slot slot, int index, int clicknb, ClickType type) {
        super.slotClicked(slot, index, clicknb, type);

        if(slot == null || slot.getItem().isEmpty())
            return;

        if(containerShop.OwnerMode && clicknb == 1 && slot.getItem().hasTag() && slot.getItem().getTag().contains("price"))
        {
            xPos = slot.x;
            yPos = slot.y;
            selectedSlot = slot;
            box.setValue(slot.getItem().getTag().getInt("price")+"");
        }
        if(containerShop.OwnerMode && clicknb == 0 && slot.getItem().hasTag() && slot.getItem().getTag().contains("price"))
        {
            selectedSlot = null;
            Messages.INSTANCE.sendToServer(new PacketChangeGhost(tileEntityShop.getBlockPos(), index, false, ItemStack.EMPTY));
        }
        if(!containerShop.OwnerMode && clicknb == 0 && slot.getItem().hasTag() && slot.getItem().getTag().contains("price"))
            Messages.INSTANCE.sendToServer(new PacketBuyItem(tileEntityShop.getBlockPos(), index, hasShiftDown()));
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


        drawSelectedSlot(matrixStack);
    }
}
