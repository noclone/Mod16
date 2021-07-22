package fr.noclone.lockdown.init;

import fr.noclone.lockdown.LockDown;
import fr.noclone.lockdown.Safe.TileEntitySafe;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, LockDown.MODID);


    public static final RegistryObject<TileEntityType<?>> SAFE_TILE_ENTITY = TILE_ENTITIES.register("safe_tile_entity",
            ()->TileEntityType.Builder.of(TileEntitySafe::new, ModBlocks.SAFE.get()).build(null));
}
