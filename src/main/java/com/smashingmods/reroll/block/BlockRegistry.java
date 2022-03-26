package com.smashingmods.reroll.block;

import com.smashingmods.reroll.Reroll;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reroll.MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reroll.MODID);

    public static final RegistryObject<Block> GRAVE_NORMAL = BLOCKS.register("grave_normal", () -> new GraveBlock(GraveModel.GRAVE_NORMAL));
    @SuppressWarnings("all")
    public static final RegistryObject<TileEntityType<?>> GRAVE_TILE = TILE_ENTITIES.register("grave_tile", () -> TileEntityType.Builder.of(GraveBlockTileEntity::new, new GraveBlock[] { (GraveBlock) GRAVE_NORMAL.get() }).build(null));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
