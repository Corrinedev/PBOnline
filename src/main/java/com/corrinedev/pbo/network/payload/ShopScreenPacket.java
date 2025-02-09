package com.corrinedev.pbo.network.payload;

import com.corrinedev.pbo.PBO;
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

public record ShopScreenPacket(String JSON) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShopScreenPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PBO.ID, "shop_screen_packet"));

    public static final StreamCodec<ByteBuf, ShopScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ShopScreenPacket::JSON,
            ShopScreenPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class HandleClient {
        public static void handleDataOnMain(final ShopScreenPacket packet, IPayloadContext iPayloadContext) {
            iPayloadContext.enqueueWork(new Runnable(){
                @Override
                public void run() {
                    JsonArray names = new Gson().fromJson(packet.JSON, JsonArray.class);
                    ArrayList<String> list = new ArrayList<>();
                    for(JsonElement element : names) list.add(element.getAsString());
                    Minecraft.getInstance().setScreen(new ShopSelectScreen(list));
                }
            });
        }
    }
}
