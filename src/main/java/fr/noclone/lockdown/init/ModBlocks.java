package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.BlockSafe;
import fr.noclone.lockdown.bankserver.BankServer;
import fr.noclone.lockdown.clearer.Clearer;
import fr.noclone.lockdown.paymentterminal.PaymentTerminal;
import fr.noclone.lockdown.shop.Shop;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LockDown.MODID);

    public static final RegistryObject<Block> STEEL_BLOCK = createBlock("steel_block",
            ()->new Block(AbstractBlock.Properties.of(Material.HEAVY_METAL).strength(20).harvestTool(ToolType.PICKAXE).harvestLevel(3).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> SAFE = createBlock("safe", BlockSafe::new);

    public static final RegistryObject<Block> BANK_SERVER = createBlock("bank_server", BankServer::new);

    public static final RegistryObject<Block> CLEARER = createBlock("clearer", Clearer::new);

    public static final RegistryObject<Block> PAYMENT_TERMINAL = createBlock("payment_terminal", PaymentTerminal::new);

    public static final RegistryObject<Block> SHOP = createBlock("shop", Shop::new);

    public static RegistryObject<Block> createBlock(String name, Supplier<? extends Block> supplier)
    {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, ()->new BlockItem(block.get(), new Item.Properties().tab(LockDown.LOCKDOWN_TAB)));
        return block;
    }
}
