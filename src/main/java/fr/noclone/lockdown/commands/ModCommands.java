package fr.noclone.lockdown.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.commands.BalanceCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> setBalanceCommand = dispatcher.register(
                Commands.literal(LockDown.MODID).then(BalanceCommand.register(dispatcher)));
        dispatcher.register(Commands.literal("lockdown").redirect(setBalanceCommand));
    }

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

}
