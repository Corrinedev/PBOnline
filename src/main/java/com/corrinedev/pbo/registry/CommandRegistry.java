package com.corrinedev.pbo.registry;

import com.corrinedev.pbo.PBO;
import com.corrinedev.pbo.network.payload.ShopScreenPacket;
import com.corrinedev.pbo.server.progression.TradeLoader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.EnumArgument;

@EventBusSubscriber(modid = PBO.ID)
public class CommandRegistry {
    @SubscribeEvent
    public static void debugShop(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("debugShop").requires((s) -> s.hasPermission(2)).executes((arg) -> {

            ServerPlayer plr = arg.getSource().getPlayerOrException();
            PacketDistributor.sendToPlayer(plr, new ShopScreenPacket(TradeLoader.fileNames()));

            return 1;
        }));
    }
    @SubscribeEvent
    public static void reload(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pboreload").requires((s) -> s.hasPermission(2)).executes((arg) -> {

            ServerPlayer plr = arg.getSource().getPlayerOrException();
            TradeLoader.registerTrades(arg.getSource().getLevel());

            return 1;
        }));
    }
    @SubscribeEvent
    public static void addReq(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("addreq").requires((s) -> s.hasPermission(2)).then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("type", EnumArgument.enumArgument(TradeLoader.Progression.class)).then(Commands.argument("int", IntegerArgumentType.integer(0)).executes((arg) -> {

            ServerPlayer plr = EntityArgument.getPlayer(arg,"player");

            switch (arg.getArgument("type", TradeLoader.Progression.class)) {
                case KILLS -> plr.getPersistentData().putInt(TradeLoader.Progression.KILLS.toString(), IntegerArgumentType.getInteger(arg, "int"));
                case HEADSHOTS -> plr.getPersistentData().putInt(TradeLoader.Progression.HEADSHOTS.toString(), IntegerArgumentType.getInteger(arg, "int"));
                case OBJECTIVES -> plr.getPersistentData().putInt(TradeLoader.Progression.OBJECTIVES.toString(), IntegerArgumentType.getInteger(arg, "int"));
                default -> arg.getSource().getPlayer().sendSystemMessage(Component.literal("INCORRECT FORMAT!"));
            }
            return 1;
        })))));
    }
}
