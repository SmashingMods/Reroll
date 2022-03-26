package com.smashingmods.reroll.data;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.block.BlockRegistry;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {

    private static ExistingFileHelper fileHelper;

    public BlockStateGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Reroll.MODID, existingFileHelper);
        fileHelper = existingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        registerGraveBlock();
    }

    private void registerGraveBlock() {
        ModelFile modelFile = new ModelFile.ExistingModelFile(modLoc("block/grave"), fileHelper);
        getVariantBuilder(BlockRegistry.GRAVE_NORMAL.get()).forAllStates(state -> {
            Direction direction = state.getValue(HorizontalFaceBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0)
                    .rotationY(direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) * 90 : 0)
                    .build();
        });
    }
}
