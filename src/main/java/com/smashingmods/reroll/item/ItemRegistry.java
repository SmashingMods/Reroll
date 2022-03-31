package com.smashingmods.reroll.item;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.block.BlockRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reroll.MODID);
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> DICE_ITEM = ITEMS.register("dice", DiceItem::new);
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> GRAVE_BLOCK_ITEM = ITEMS.register("grave", () -> new BlockItem(BlockRegistry.GRAVE_NORMAL.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
