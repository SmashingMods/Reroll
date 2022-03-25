package com.smashingmods.reroll.block;

import com.smashingmods.reroll.Reroll;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistry {

    @ObjectHolder(Reroll.MODID + ":grave_normal")
    public static GraveBlock GRAVE_NORMAL;

    @ObjectHolder(Reroll.MODID + ":grave")
    public static TileEntityType<GraveBlockTileEntity> GRAVE_TILE;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new GraveBlock(GraveModel.GRAVE_NORMAL).setRegistryName("grave_normal"));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.of(GraveBlockTileEntity::new, new GraveBlock[] { BlockRegistry.GRAVE_NORMAL }).build(null).setRegistryName("grave"));
    }
}
