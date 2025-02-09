package com.corrinedev.pbo.network.payload;

import com.corrinedev.pbo.PBO;
import com.corrinedev.pbo.server.progression.TradeLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Set;

public record UnlockPacket(String itemId, String prog) implements CustomPacketPayload {
    public static final Type<UnlockPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PBO.ID, "unlock_packet"));

    public static final StreamCodec<ByteBuf, UnlockPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            UnlockPacket::itemId,
            ByteBufCodecs.STRING_UTF8,
            UnlockPacket::prog,
            UnlockPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class HandleServer {
        public static void handleDataOnMain(final UnlockPacket packet, IPayloadContext iPayloadContext) {
            iPayloadContext.enqueueWork(new Runnable(){
                @Override
                public void run() {
                    if(iPayloadContext.player() instanceof ServerPlayer sv) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        LinkedList<TradeLoader.ItemInfo> infos = TradeLoader.TYPES.get(packet.prog);
                        JsonArray arr = new JsonArray();

                        for (TradeLoader.ItemInfo info : infos) {
                            if(info.item().equals(packet.itemId)) {
                                if(info.req() > sv.getPersistentData().getInt(info.type().toString())) {sv.displayClientMessage(Component.literal("You don't have enough " + info.type().toString()), true); return;}
                                if(info.unlocked().contains(sv.getStringUUID())){
                                    if(sv.getInventory().hasAnyOf(Set.of(sv.getServer().registryAccess().registry(Registries.ITEM).get().get(ResourceLocation.parse(info.item()))))) {
                                        sv.addItem(new ItemStack(sv.getServer().registryAccess().registry(Registries.ITEM).get().get(ResourceLocation.parse(info.item()))));
                                    } else {sv.displayClientMessage(Component.literal("You already have one of those, put it on a gun if its an attachment!"), false);}

                                    return;
                                }
                                info.unlocked().add(sv.getStringUUID());
                            }
                            arr.add(gson.toJsonTree(info, TradeLoader.ItemInfo.class));
                        }
                        PacketDistributor.sendToPlayer(sv, new ProgressionScreenPacket(gson.toJson(arr), packet.prog));
                        System.out.println(arr);
                        File file = FMLPaths.CONFIGDIR.get().resolve("pbo").resolve("progression").resolve(packet.prog + ".json").toFile();
                        try {
                            Files.writeString(file.toPath(), gson.toJson(arr));
                            TradeLoader.readTrades(sv.serverLevel());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }
}
