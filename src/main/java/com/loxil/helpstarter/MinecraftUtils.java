package com.loxil.helpstarter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public final class MinecraftUtils {

    private MinecraftUtils() {
    }

    public static String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public static void addChatMessageOnMainThread(final String message) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
                }
            }
        });
    }

    public static void sendChatMessageOnMainThread(final String message) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
                }
            }
        });
    }
}