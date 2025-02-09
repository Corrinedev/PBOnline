package com.corrinedev.pbo.shop.client;

import com.corrinedev.pbo.network.Network;
import com.corrinedev.pbo.network.payload.UnlockPacket;
import com.corrinedev.pbo.server.progression.TradeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class ProgressionScreen extends Screen {
    LinkedList<TradeLoader.ItemInfo> infoList;
    public long lastSysTime;
    public long sysTime;
    public int page;
    public String name;
    private static final int maxPg = 4;

    public ProgressionScreen(LinkedList<TradeLoader.ItemInfo> info, String name) {
        super(Component.literal(name));
        infoList = info;
        this.lastSysTime = System.currentTimeMillis();
        this.sysTime = System.currentTimeMillis();
        page = 0;
        this.name = name;
    }
    public static float getSmoothValue(float value, float target, float speed) {
        return value + (target - value) * speed;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        if(infoList.size() >= (page * maxPg)) {
            for (int i = 0; i < maxPg; i++) {
                if (i < infoList.size()) {
                    Item item = Minecraft.getInstance().level.registryAccess().registry(Registries.ITEM).get().get(ResourceLocation.parse(infoList.get(i).item()));
                    addRenderableWidget(new Button.Builder(item.getName(item.getDefaultInstance()), (button) -> {
                        Minecraft.getInstance().setScreen(null);
                        //SEND PACKET
                        PacketDistributor.sendToServer(new UnlockPacket(item.toString(), this.name));
                    }).bounds((80) + (i * 85), (height / 2) - 77, 80, 20).build());
                }
            }
        }

    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        //guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("pbo", "textures/gui/shop.png"), (width / 2) - 256 / 2, (height / 2) - 256 / 2, 0, 0, 256, 256);

    }
}
