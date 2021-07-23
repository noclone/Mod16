package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.ContainerSafe;
import fr.noclone.lockdown.Safe.ContainerSafeLocked;
import fr.noclone.lockdown.Safe.SafeScreen;
import fr.noclone.lockdown.Safe.SafeScreenLocked;
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


    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event)
    {
        ScreenManager.register(SAFE.get(), SafeScreen::new);
        ScreenManager.register(SAFE_LOCKED.get(), SafeScreenLocked::new);
    }

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String name, IContainerFactory factory)
    {
        return CONTAINERS.register(name, ()-> IForgeContainerType.create(factory));
    }
}
