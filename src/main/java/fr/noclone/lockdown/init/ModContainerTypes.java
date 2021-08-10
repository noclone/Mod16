package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.ContainerSafe;
import fr.noclone.lockdown.Safe.ContainerSafeLocked;
import fr.noclone.lockdown.Safe.SafeScreen;
import fr.noclone.lockdown.Safe.SafeScreenLocked;
import fr.noclone.lockdown.bankserver.BankServerScreen;
import fr.noclone.lockdown.bankserver.ContainerBankServer;
import fr.noclone.lockdown.clearer.ClearerScreen;
import fr.noclone.lockdown.clearer.ContainerClearer;
import fr.noclone.lockdown.paymentterminal.ContainerPaymentTerminal;
import fr.noclone.lockdown.paymentterminal.PaymentTerminalScreen;
import fr.noclone.lockdown.shop.ContainerShop;
import fr.noclone.lockdown.shop.ShopScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.system.CallbackI;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, LockDown.MODID);

    public static final RegistryObject<ContainerType<ContainerSafe>> SAFE = register("safe", ContainerSafe::new);
    public static final RegistryObject<ContainerType<ContainerSafeLocked>> SAFE_LOCKED = register("safe_locked", ContainerSafeLocked::new);

    public static final RegistryObject<ContainerType<ContainerBankServer>> BANK_SERVER = register("bank_server", ContainerBankServer::new);

    public static final RegistryObject<ContainerType<ContainerClearer>> CLEARER = register("clearer", ContainerClearer::new);

    public static final RegistryObject<ContainerType<ContainerPaymentTerminal>> PAYMENT_TERMINAL = register("payment_terminal", ContainerPaymentTerminal::new);

    public static final RegistryObject<ContainerType<ContainerShop>> SHOP = register("shop", ContainerShop::new);


    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event)
    {
        ScreenManager.register(SAFE.get(), SafeScreen::new);
        ScreenManager.register(SAFE_LOCKED.get(), SafeScreenLocked::new);
        ScreenManager.register(BANK_SERVER.get(), BankServerScreen::new);
        ScreenManager.register(CLEARER.get(), ClearerScreen::new);
        ScreenManager.register(PAYMENT_TERMINAL.get(), PaymentTerminalScreen::new);
        ScreenManager.register(SHOP.get(), ShopScreen::new);
    }

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String name, IContainerFactory factory)
    {
        return CONTAINERS.register(name, ()-> IForgeContainerType.create(factory));
    }
}
