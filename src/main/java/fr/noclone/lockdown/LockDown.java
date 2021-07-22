package fr.noclone.lockdown;


import fr.noclone.lockdown.init.ModBlocks;
import fr.noclone.lockdown.init.ModContainerTypes;
import fr.noclone.lockdown.init.ModItems;
import fr.noclone.lockdown.init.ModTileEntities;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LockDown.MODID)
public class LockDown {

    public static final String MODID = "lockdown";

    public static final ItemGroup LOCKDOWN_TAB = new ItemGroup("lockdown_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.STEEL_INGOT.get());
        }
    };

    public LockDown() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModTileEntities.TILE_ENTITIES.register(bus);
        ModContainerTypes.CONTAINERS.register(bus);
    }

    private void setup(FMLCommonSetupEvent e)
    {

    }

    private void clientSetup(FMLCommonSetupEvent e)
    {

    }

    @Mod.EventBusSubscriber(modid = LockDown.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Client{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ModContainerTypes.registerScreens(event);
        }

    }
}