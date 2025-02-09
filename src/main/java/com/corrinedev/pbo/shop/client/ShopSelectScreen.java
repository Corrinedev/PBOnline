package com.corrinedev.pbo.shop.client;

import com.corrinedev.pbo.network.payload.SendItemInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;

public class ShopSelectScreen extends Screen {
    public final List<String> OFFERS;
    public long lastSysTime;
    public long sysTime;
    public HashMap<String, Integer> offersRandomizer = new HashMap<>();

    public ShopSelectScreen(List<String> offers) {
        super(Component.literal("Shop"));
        this.OFFERS = offers;
        this.lastSysTime = System.currentTimeMillis();
        this.sysTime = System.currentTimeMillis();
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
        int i = 0;
        for (String key : OFFERS) {
            if(i <= 6) {
                addRenderableWidget(new Button.Builder(Component.literal(key), (button) -> {
                    Minecraft.getInstance().setScreen(null);

                    //SEND PACKET
                    PacketDistributor.sendToServer(new SendItemInfoPacket(key));
                })
                        .bounds((width / 2) - 82, (height / 2 + (i * 22)) - 77, 80, 20).build());
            } else {
                addRenderableWidget(new Button.Builder(Component.literal(key), (button) -> {
                    Minecraft.getInstance().setScreen(null);
                    //SEND PACKET
                    PacketDistributor.sendToServer(new SendItemInfoPacket(key));
                })
                        .bounds((width / 2) + 2, (height / 2 + ((i - 7) * 22)) - 77, 80, 20).build());
            }
            i++;
        }

    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("pbo", "textures/gui/shop.png"), (width / 2) - 256 / 2, (height / 2) - 256 / 2, 0, 0, 256, 256);

    }
}
