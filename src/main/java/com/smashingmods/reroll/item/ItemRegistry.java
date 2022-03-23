package com.smashingmods.reroll.item;

import com.smashingmods.reroll.Reroll;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reroll.MODID);
    public static final RegistryObject<Item> DICE_ITEM = ITEMS.register("dice", () -> new DiceItem(new Item.Properties().stacksTo(1).setNoRepair().durability(1).rarity(Rarity.EPIC).fireResistant().tab(ItemGroup.TAB_MISC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
