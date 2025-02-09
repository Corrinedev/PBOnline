package com.corrinedev.pbo.network.payload;

import com.corrinedev.pbo.PBO;
import com.corrinedev.pbo.server.progression.TradeLoader;
import com.corrinedev.pbo.shop.client.ProgressionScreen;
import com.corrinedev.pbo.shop.client.ShopSelectScreen;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

public record ProgressionScreenPacket(String JSON, String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ProgressionScreenPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PBO.ID, "progression_screen_packet"));

    public static final StreamCodec<ByteBuf, ProgressionScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ProgressionScreenPacket::JSON,
            ByteBufCodecs.STRING_UTF8,
            ProgressionScreenPacket::name,
            ProgressionScreenPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class HandleClient {
        public static void handleDataOnMain(final ProgressionScreenPacket packet, IPayloadContext iPayloadContext) {
            iPayloadContext.enqueueWork(new Runnable(){
                @Override
                public void run() {
                    Gson gson = new Gson();
                    JsonArray itemInfos = gson.fromJson(packet.JSON, JsonArray.class);
                    LinkedList<TradeLoader.ItemInfo> list = new LinkedList<>();
                    for(JsonElement element : itemInfos) list.add(gson.fromJson(element, TradeLoader.ItemInfo.class));
                    Minecraft.getInstance().setScreen(new ProgressionScreen(list, packet.name));
                }
            });
        }
    }
}
