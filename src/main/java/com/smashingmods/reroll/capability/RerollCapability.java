package com.smashingmods.reroll.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class RerollCapability {

    @CapabilityInject(IRerollCapability.class)
    public static Capability<IRerollCapability> REROLL_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                IRerollCapability.class,
                new RerollCapabilityStorage(),
                RerollCapabilityImpl::new
        );
    }
}
