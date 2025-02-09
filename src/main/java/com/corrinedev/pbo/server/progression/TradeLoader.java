package com.corrinedev.pbo.server.progression;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class TradeLoader {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path DIR = FMLPaths.CONFIGDIR.get().resolve("pbo").resolve("progression");
    public static LinkedHashMap<String, LinkedList<ItemInfo>> TYPES = new LinkedHashMap<>();
    public static String fileNames() {
        JsonArray obj = new JsonArray();
        for(File file : DIR.toFile().listFiles()) {
            obj.add(file.getName().split("\\.")[0]);
        }
        return GSON.toJson(obj);
    }

    public static void registerTrades(ServerLevel level) {
        try {
            readTrades(level);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readTrades(ServerLevel level) throws IOException {
        TYPES = new LinkedHashMap<>();
        File dir = DIR.toFile();
        if(!dir.exists()) dir.mkdirs();

        for (File file : dir.listFiles() != null ? dir.listFiles() : new File[]{}) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                JsonReader reader = new JsonReader(new FileReader(file));
                JsonArray arr = GSON.fromJson(reader, JsonArray.class);
                    LinkedList<ItemInfo> items = new LinkedList<>();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject obj = arr.get(i).getAsJsonObject();
                        Progression progression = Progression.of(obj.get("type").getAsString());
                        Item item = level.getServer().registryAccess().registry(Registries.ITEM).get().get(ResourceLocation.parse(obj.get("item").getAsString()));
                        System.out.println("ITEM ACCEPTED = " + item);
                        int requirement = obj.get("req").getAsInt();
                        Set<String> uuids = new HashSet<>();
                        for (JsonElement element : obj.get("unlocked").getAsJsonArray()) {
                            uuids.add(element.getAsString());
                        }
                        items.add(new ItemInfo(item.toString(), progression, requirement, uuids));
                    }
                    TYPES.put(file.getName().split("\\.")[0], items);
                }
            }
    }
    public record ItemInfo(String item, Progression type, int req, Set<String> unlocked) {

    }
    public enum Progression {
        KILLS,
        HEADSHOTS,
        OBJECTIVES;

        public static Progression of(String name) {
            return switch (name.toUpperCase()) {
                case "KILLS" -> KILLS;
                case "HEADSHOTS" -> HEADSHOTS;
                case "OBJECTIVES" -> OBJECTIVES;
                default ->
                    throw new IllegalStateException("Unexpected value: " + name);
            };
        }
    }
}
