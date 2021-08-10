package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.creditcard.CreditCard;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.system.CallbackI;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LockDown.MODID);

    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot",()->new Item(new Item.Properties()
            .tab(LockDown.LOCKDOWN_TAB)));
    public static final RegistryObject<Item> STEEL_BAR = ITEMS.register("steel_bar",()->new Item(new Item.Properties()
            .tab(LockDown.LOCKDOWN_TAB)));

    public static final RegistryObject<CreditCard> CREDIT_CARD = ITEMS.register("credit_card", ()->new CreditCard(new Item.Properties()
            .tab(LockDown.LOCKDOWN_TAB).stacksTo(1)));
}