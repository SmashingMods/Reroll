package com.smashingmods.reroll.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class LockCapability {

    @CapabilityInject(LockCapabilityInterface.class)
    public static Capability<LockCapabilityImplementation> CAPABILITY_LOCK = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                LockCapabilityInterface.class,
                new LockCapabilityStorage(),
                LockCapabilityImplementation::new
        );
    }
}
