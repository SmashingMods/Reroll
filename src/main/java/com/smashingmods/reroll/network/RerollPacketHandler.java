package com.smashingmods.reroll.network;

import com.smashingmods.reroll.Reroll;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RerollPacketHandler {

    private static int PACKET_ID = 0;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reroll.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        INSTANCE.messageBuilder(RerollPacket.class, PACKET_ID++).encoder(RerollPacket::encode).decoder(RerollPacket::new).consumer(ServerMessageHandler::handle).add();
    }
}
