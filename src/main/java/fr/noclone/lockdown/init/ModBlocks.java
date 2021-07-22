package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.BlockSafe;
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
            ()->new Block(AbstractBlock.Properties.of(Material.HEAVY_METAL).strength(3).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> SAFE = createBlock("safe", BlockSafe::new);

    public static RegistryObject<Block> createBlock(String name, Supplier<? extends Block> supplier)
    {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, ()->new BlockItem(block.get(), new Item.Properties().tab(LockDown.LOCKDOWN_TAB)));
        return block;
    }
}
