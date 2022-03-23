package com.smashingmods.reroll.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class RerollCapability {

    @CapabilityInject(RerollCapabilityInterface.class)
    public static Capability<RerollCapabilityImplementation> REROLL_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                RerollCapabilityInterface.class,
                new RerollCapabilityStorage(),
                RerollCapabilityImplementation::new
        );
    }
}
