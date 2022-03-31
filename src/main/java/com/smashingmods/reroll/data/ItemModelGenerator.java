package com.smashingmods.reroll.data;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.item.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Reroll.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Objects.requireNonNull(ItemRegistry.GRAVE_BLOCK_ITEM.get().getRegistryName()).getPath(), new ResourceLocation(Reroll.MODID, "block/grave"));
    }
}
