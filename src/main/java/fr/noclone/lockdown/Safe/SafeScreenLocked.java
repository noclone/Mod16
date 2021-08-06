package fr.noclone.lockdown.Safe;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.network.Messages;
import fr.noclone.lockdown.network.PacketSyncSafe;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SafeScreenLocked extends ContainerScreen<ContainerSafeLocked> {
    public static final ResourceLocation LOCKED = new ResourceLocation(LockDown.MODID, "textures/gui/safe_lock.png");

    ContainerSafeLocked containerSafeLocked;

    TileEntitySafe tileEntitySafe;

    String password = "";

    TextFieldWidget box;

    boolean isOwner;

    boolean NewSet = false;

    int wait = 0;

    public SafeScreenLocked(ContainerSafeLocked containerSafeLocked, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerSafeLocked, playerInventory, textComponent);

        this.containerSafeLocked = containerSafeLocked;

        IIntArray fields = containerSafeLocked.getFields();
        TileEntity te = Minecraft.getInstance().level.getBlockEntity(new BlockPos(fields.get(1),fields.get(2),fields.get(3)));
        if(te instanceof TileEntitySafe)
            tileEntitySafe = (TileEntitySafe) te;

        tileEntitySafe.setOwner(containerSafeLocked.getTileEntitySafe().getOwner());

        if(tileEntitySafe.getOwner().equals(Minecraft.getInstance().player.getUUID()))
            isOwner = true;
    }

    @Override
    protected void init() {
        super.init();

        buttons.clear();
        addButton(new Button(getGuiLeft()+63,getGuiTop()+69,18,18,new TranslationTextComponent("7"),
                (button)->{onNumberClicked(7);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69,18,18,new TranslationTextComponent("8"),
                (button)->{onNumberClicked(8);}));
        addButton(new Button(getGuiLeft()+63+18*2,getGuiTop()+69,18,18,new TranslationTextComponent("9"),
                (button)->{onNumberClicked(9);}));
        addButton(new Button(getGuiLeft()+63,getGuiTop()+69+18,18,18,new TranslationTextComponent("4"),
                (button)->{onNumberClicked(4);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69+18,18,18,new TranslationTextComponent("5"),
                (button)->{onNumberClicked(5);}));
        addButton(new Button(getGuiLeft()+63+18*2,getGuiTop()+69+18,18,18,new TranslationTextComponent("6"),
                (button)->{onNumberClicked(6);}));
        addButton(new Button(getGuiLeft()+63,getGuiTop()+69+18*2,18,18,new TranslationTextComponent("1"),
                (button)->{onNumberClicked(1);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69+18*2,18,18,new TranslationTextComponent("2"),
                (button)->{onNumberClicked(2);}));
        addButton(new Button(getGuiLeft()+63+18*2,getGuiTop()+69+18*2,18,18,new TranslationTextComponent("3"),
                (button)->{onNumberClicked(3);}));
        addButton(new Button(getGuiLeft()+63+18,getGuiTop()+69+18*3,18,18,new TranslationTextComponent("0"),
                (button)->{onNumberClicked(0);}));


        int textfieldW = 50;
        int textfieldH = 20;
        box = new TextFieldWidget(font,getGuiLeft()+imageWidth/2-textfieldW/2+1, getGuiTop()+textfieldH+20, textfieldW, textfieldH,new TranslationTextComponent(""));
        box.setVisible(true);
        box.setMaxLength(4);
        addWidget(box);
        addButton(new Button(box.x+box.getWidth()+5,box.y,40,20,new TranslationTextComponent("Enter"),
                (button)->{enter();}));

        addButton(new Button(box.x+box.getWidth()+5,getGuiTop()+69,18,18,new TranslationTextComponent("<-"),
                (button)->{del();}));

        if(isOwner)
        {
            addButton(new Button(box.x-45,box.y,40,20,new TranslationTextComponent("Set"),
                    (button)->{setNewPassword();}));
        }
    }

    private void del() {
        if(!box.getValue().isEmpty())
            box.setValue(box.getValue().substring(0, box.getValue().length()-1));
    }

    private void setNewPassword() {
        password = box.getValue();
        tileEntitySafe.setCorrectPassword(password);
        SendUpdatesToServer();
        NewSet = true;
        wait = LocalTime.now().getSecond();
    }

    private void SendUpdatesToServer()
    {
        Messages.INSTANCE.sendToServer(new PacketSyncSafe(tileEntitySafe.isUnlocked(), tileEntitySafe.getCorrectPassword(), tileEntitySafe.getOwner()));
    }

    private void enter() {
        password = box.getValue();
        if(password.equals(tileEntitySafe.getCorrectPassword()))
        {
            tileEntitySafe.setUnlocked(true);
            SendUpdatesToServer();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.player.closeContainer();
            minecraft.player.playSound(SoundEvents.ANVIL_BREAK,1.0f,1.0f);
            BlockState state = minecraft.level.getBlockState(tileEntitySafe.getBlockPos());
            minecraft.level.setBlock(tileEntitySafe.getBlockPos(), state.setValue(BlockStateProperties.LOCKED, false),
                    Constants.BlockFlags.NOTIFY_NEIGHBORS+Constants.BlockFlags.BLOCK_UPDATE);
        }
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
        if(box != null)
            box.render(matrixStack, x, y, partialTicks);
        if(wait != 0 && LocalTime.now().getSecond() - wait > 2)
        {
            wait = 0;
            NewSet = false;
        }
        if(NewSet)
            drawCenteredString(matrixStack,font,new TranslationTextComponent("New Password Set !"), getGuiLeft()+imageWidth/2, getGuiTop()+20, 0x52FF33);
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
        box.insertText(nb+"");
    }
}
