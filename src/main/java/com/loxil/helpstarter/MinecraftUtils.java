package com.loxil.helpstarter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MinecraftUtils {

    private MinecraftUtils() {
    }

    public static String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public static List<String> getOnlinePlayerNames() {
        List<String> playerNames = new ArrayList<String>();
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.sendQueue != null) {
            Collection<NetworkPlayerInfo> playerInfoList = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap();
            for (NetworkPlayerInfo playerInfo : playerInfoList) {
                playerNames.add(playerInfo.getGameProfile().getName());
            }
        }
        return playerNames;
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