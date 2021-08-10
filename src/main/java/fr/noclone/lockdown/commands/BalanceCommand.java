package fr.noclone.lockdown.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.creditcard.CreditCard;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BalanceCommand implements Command<CommandSource> {

    private static final BalanceCommand CMD = new BalanceCommand();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher)
    {
        return Commands.literal("setbalance")
                .requires(cs -> cs.hasPermission(3))
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                .executes(CMD));
    }


    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        Entity entity = context.getSource().getEntity();
        if(entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) entity;
            ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
            if(stack.getItem() instanceof CreditCard)
            {
                if(stack.hasTag() && stack.getTag().contains("serverX"))
                {
                    CompoundNBT tag = stack.getTag();
                    TileEntity te = player.level.getBlockEntity(new BlockPos(tag.getInt("serverX"),tag.getInt("serverY"),tag.getInt("serverZ")));
                    if(te instanceof TileEntityBankServer)
                    {
                        TileEntityBankServer server = (TileEntityBankServer) te;
                        for(int i = 0; i < TileEntityBankServer.MAX_CARDS; i++)
                        {
                            if(!server.getCards().get(i).isEmpty() && server.getCards().get(i).getTag().getUUID("id").equals(stack.getTag().getUUID("id")))
                            {
                                server.getCards().get(i).getTag().putInt("balance", amount);
                                context.getSource().sendSuccess(new TranslationTextComponent(TextFormatting.GREEN +"New Amount Set !"), false);
                            }
                        }
                    }
                    else
                        context.getSource().sendFailure(new TranslationTextComponent(TextFormatting.RED +"Bank Server has been moved or destroyed !"));
                }
                else
                    context.getSource().sendFailure(new TranslationTextComponent(TextFormatting.RED +"Card not linked to an account !"));
            }
            else
                context.getSource().sendFailure(new TranslationTextComponent(TextFormatting.RED +"Not a credit card !"));
        }
        return 0;
    }
}
