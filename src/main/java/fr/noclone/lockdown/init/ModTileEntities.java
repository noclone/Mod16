package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.TileEntitySafe;
import fr.noclone.lockdown.bankserver.TileEntityBankServer;
import fr.noclone.lockdown.clearer.TileEntityClearer;
import fr.noclone.lockdown.paymentterminal.TileEntityPaymentTerminal;
import fr.noclone.lockdown.shop.TileEntityShop;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, LockDown.MODID);


    public static final RegistryObject<TileEntityType<?>> SAFE_TILE_ENTITY = TILE_ENTITIES.register("safe_tile_entity",
            ()->TileEntityType.Builder.of(TileEntitySafe::new, ModBlocks.SAFE.get()).build(null));

    public static final RegistryObject<TileEntityType<?>> BANK_SERVER_TILE_ENTITY = TILE_ENTITIES.register("bank_server_tile_entity",
            ()->TileEntityType.Builder.of(TileEntityBankServer::new, ModBlocks.BANK_SERVER.get()).build(null));

    public static final RegistryObject<TileEntityType<?>> CLEARER_TILE_ENTITY = TILE_ENTITIES.register("clearer_tile_entity",
            ()->TileEntityType.Builder.of(TileEntityClearer::new, ModBlocks.CLEARER.get()).build(null));

    public static final RegistryObject<TileEntityType<?>> PAYMENT_TERMINAL_TILE_ENTITY = TILE_ENTITIES.register("payment_terminal_tile_entity",
            ()->TileEntityType.Builder.of(TileEntityPaymentTerminal::new, ModBlocks.PAYMENT_TERMINAL.get()).build(null));

    public static final RegistryObject<TileEntityType<?>> SHOP_TILE_ENTITY = TILE_ENTITIES.register("shop_tile_entity",
            ()->TileEntityType.Builder.of(TileEntityShop::new, ModBlocks.SHOP.get()).build(null));
}
