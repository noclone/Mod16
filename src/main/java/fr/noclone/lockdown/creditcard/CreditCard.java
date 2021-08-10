package fr.noclone.lockdown.creditcard;

import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import javax.swing.plaf.TabbedPaneUI;
import java.util.List;
import java.util.UUID;

import static fr.noclone.lockdown.utils.Utils.formatInt;

public class CreditCard extends Item {

    public CreditCard(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(ItemStack p_77622_1_, World p_77622_2_, PlayerEntity p_77622_3_) {
        super.onCraftedBy(p_77622_1_, p_77622_2_, p_77622_3_);
        p_77622_1_.setTag(new CompoundNBT());
        p_77622_1_.getTag().putUUID("banker", p_77622_3_.getUUID());
        p_77622_1_.getTag().putUUID("id", UUID.randomUUID());
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        if(!stack.hasTag())
        {
            CompoundNBT tag = new CompoundNBT();
            tag.putUUID("banker", context.getPlayer().getUUID());
            tag.putUUID("id", UUID.randomUUID());
            stack.setTag(tag);
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(itemStack, world, p_77663_3_, p_77663_4_, p_77663_5_);

        if(itemStack.hasTag())
        {
            CompoundNBT tag = itemStack.getTag();

            TileEntity te = world.getBlockEntity(new BlockPos(tag.getInt("serverX"), tag.getInt("serverY"), tag.getInt("serverZ")));

            if(te instanceof TileEntityBankServer) {
                TileEntityBankServer server = (TileEntityBankServer) te;
                for (int i = 0; i < TileEntityBankServer.MAX_CARDS; i++) {

                    if (itemStack.getTag().contains("id") && server.getCards().get(i).hasTag() && server.getCards().get(i).getTag().getUUID("id").equals(itemStack.getTag().getUUID("id"))) {
                        break;
                    }
                    if(i == TileEntityBankServer.MAX_CARDS-1)
                    {
                        itemStack.setTag(Unlink(itemStack.getTag()));
                    }
                }
            }
            else
            {
                itemStack.setTag(Unlink(itemStack.getTag()));
            }
        }
    }

    private CompoundNBT Unlink(CompoundNBT tag)
    {
        if(tag.contains("owner"))
            tag.remove("owner");
        if(tag.contains("balance"))
            tag.remove("balance");
        if(tag.contains("serverX"))
        {
            tag.remove("serverX");
            tag.remove("serverY");
            tag.remove("serverZ");
        }
        return tag;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(itemStack, world, tooltip, flag);
        if(itemStack.hasTag())
        {
            CompoundNBT tag = itemStack.getTag();
            if(tag.contains("serverX"))
            {
                TileEntity te = world.getBlockEntity(new BlockPos(tag.getInt("serverX"), tag.getInt("serverY"), tag.getInt("serverZ")));
                if(te instanceof TileEntityBankServer)
                {
                    TileEntityBankServer server = (TileEntityBankServer) te;
                    for(int i = 0; i < TileEntityBankServer.MAX_CARDS; i++)
                    {
                        if(itemStack.getTag().contains("id") && server.getCards().get(i).hasTag() && server.getCards().get(i).getTag().getUUID("id").equals(itemStack.getTag().getUUID("id")))
                        {
                            itemStack.getTag().putInt("balance", server.getCards().get(i).getTag().getInt("balance"));
                            break;
                        }
                    }
                }
            }
            String amount = Screen.hasShiftDown() ? itemStack.getTag().getInt("balance")+"" : formatInt(itemStack.getTag().getInt("balance"));
            if(tag.contains("balance"))
                tooltip.add(new TranslationTextComponent(TextFormatting.AQUA+"Balance : "+TextFormatting.GOLD+amount+"$"));
            if(tag.contains("owner"))
                tooltip.add(new TranslationTextComponent(TextFormatting.BLUE+"Owner : "+TextFormatting.DARK_AQUA+world.getPlayerByUUID(tag.getUUID("owner")).getScoreboardName()));
            if(tag.contains("banker"))
                tooltip.add(new TranslationTextComponent(TextFormatting.LIGHT_PURPLE+"Banker : "+TextFormatting.DARK_PURPLE+world.getPlayerByUUID(itemStack.getTag().getUUID("banker")).getScoreboardName()));
        }
    }
}
