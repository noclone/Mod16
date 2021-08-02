package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.TileEntitySafe;
import fr.noclone.lockdown.bankserver.TileEntityBankServer;
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
}
