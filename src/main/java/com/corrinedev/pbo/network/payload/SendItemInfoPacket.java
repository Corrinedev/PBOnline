package com.corrinedev.pbo.network.payload;

import com.corrinedev.pbo.PBO;
import com.corrinedev.pbo.network.Network;
import com.corrinedev.pbo.server.progression.TradeLoader;
import com.corrinedev.pbo.shop.client.ShopSelectScreen;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

public record SendItemInfoPacket(String name) implements CustomPacketPayload {
    public static final Type<SendItemInfoPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PBO.ID, "item_info"));

    public static final StreamCodec<ByteBuf, SendItemInfoPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SendItemInfoPacket::name,
            SendItemInfoPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class HandleServer {
        public static void handleDataOnMain(final SendItemInfoPacket packet, IPayloadContext iPayloadContext) {
            iPayloadContext.enqueueWork(new Runnable(){
                @Override
                public void run() {
                    if(iPayloadContext.player() instanceof ServerPlayer sv) {
                        Gson gson = new Gson();
                        JsonArray arr = new JsonArray();
                        LinkedList<TradeLoader.ItemInfo> infoList = TradeLoader.TYPES.get(packet.name);

                        for (TradeLoader.ItemInfo itemInfo : infoList) {
                            JsonObject obj = gson.toJsonTree(itemInfo, TradeLoader.ItemInfo.class).getAsJsonObject();
                            arr.add(obj);
                        }

                        PacketDistributor.sendToPlayer(sv, new ProgressionScreenPacket(gson.toJson(arr),packet.name));
                    }
                }
            });
        }
    }
}
