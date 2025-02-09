package com.corrinedev.pbo.network;

import com.corrinedev.pbo.PBO;
import com.corrinedev.pbo.network.payload.ProgressionScreenPacket;
import com.corrinedev.pbo.network.payload.SendItemInfoPacket;
import com.corrinedev.pbo.network.payload.ShopScreenPacket;
import com.corrinedev.pbo.network.payload.UnlockPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = PBO.ID, bus = EventBusSubscriber.Bus.MOD)
public class Network {

    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                ShopScreenPacket.TYPE,
                ShopScreenPacket.STREAM_CODEC,
                ShopScreenPacket.HandleClient::handleDataOnMain
        );
        registrar.playToClient(
                ProgressionScreenPacket.TYPE,
                ProgressionScreenPacket.STREAM_CODEC,
                ProgressionScreenPacket.HandleClient::handleDataOnMain
        );
        registrar.playToServer(
                SendItemInfoPacket.TYPE,
                SendItemInfoPacket.STREAM_CODEC,
                SendItemInfoPacket.HandleServer::handleDataOnMain
        );
        registrar.playToServer(
                UnlockPacket.TYPE,
                UnlockPacket.STREAM_CODEC,
                UnlockPacket.HandleServer::handleDataOnMain
        );
    }
}
