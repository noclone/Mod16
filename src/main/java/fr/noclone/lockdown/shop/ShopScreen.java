package fr.noclone.lockdown.shop;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.clearer.ContainerClearer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import fr.noclone.lockdown.creditcard.CreditCard;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketChangeGhost;
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


@Mod.EventBusSubscriber
public class ShopScreen extends ContainerScreen<ContainerShop> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(LockDown.MODID, "textures/gui/shop.png");
    public static final ResourceLocation SELECTEDSLOT = new ResourceLocation(LockDown.MODID, "textures/gui/selectedslot.png");

    TileEntityShop tileEntityShop;

    ContainerShop containerShop;

    TextFieldWidget box;

    int xPos = -1;
    int yPos = -1;

    Slot selectedSlot;

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

        if(tileEntityShop.getOwner().equals(Minecraft.getInstance().player.getUUID()))
        {
            buttons.clear();
            addButton(new Button(getGuiLeft()+8,getGuiTop()+22, 18, 18, new TranslationTextComponent("-")
                    , (button)->{PlusMinus(-1);}));
            addButton(new Button(getGuiLeft()+72,getGuiTop()+22, 18, 18, new TranslationTextComponent("+")
                    , (button)->{PlusMinus(1);}));

            int textfieldW = 44;
            int textfieldH = 18;
            box = new TextFieldWidget(font,getGuiLeft()+27, getGuiTop()+22, textfieldW, textfieldH,new TranslationTextComponent(""));
            box.setVisible(true);
            box.setValue("0");
            addWidget(box);


            addButton(new Button(getGuiLeft()+95,getGuiTop()+22, 30, 18, new TranslationTextComponent("Set")
                    , (button)->{SetPrice();}));

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

        if(box != null)
            box.render(matrixStack, x, y, partialTicks);



        this.renderTooltip(matrixStack, x, y);
    }

    private void SetPrice() {
        if(selectedSlot == null || !IsValid())
            return;
        selectedSlot.getItem().getTag().putInt("price", Integer.parseInt(box.getValue()));
    }

    private void drawSelectedSlot(MatrixStack matrixStack) {
        if(xPos == -1)
            return;

        minecraft.getTextureManager().bind(SELECTEDSLOT);
        blit(matrixStack, getGuiLeft()+xPos-1, getGuiTop()+yPos-1, 0, 0, 256, 256);
    }

    @Override
    protected void slotClicked(Slot slot, int index, int clicknb, ClickType type) {
        if(slot == null)
            return;
        if(tileEntityShop.getOwner().equals(Minecraft.getInstance().player.getUUID()) && clicknb == 1 && slot.getItem().hasTag() && slot.getItem().getTag().contains("price"))
        {
            xPos = slot.x;
            yPos = slot.y;
            selectedSlot = slot;
            box.setValue(slot.getItem().getTag().getInt("price")+"");
        }
        if(tileEntityShop.getOwner().equals(Minecraft.getInstance().player.getUUID()) && clicknb == 0 && slot.getItem().hasTag() && slot.getItem().getTag().contains("price"))
        {
            Messages.INSTANCE.sendToServer(new PacketChangeGhost(tileEntityShop.getBlockPos(), index, false, ItemStack.EMPTY));
        }
        super.slotClicked(slot, index, clicknb, type);
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


    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if(stack.hasTag() && stack.getTag().contains("price"))
        {
            event.getToolTip().add(new TranslationTextComponent(TextFormatting.GOLD+""+stack.getTag().getInt("price")+"$"));
        }
    }
}
