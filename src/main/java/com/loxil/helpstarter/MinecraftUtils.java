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

    /**
     * Validates if a string is a valid Minecraft username.
     * Minecraft usernames must be 3-16 characters long and contain only letters, numbers, and underscores.
     *
     * @param username
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,16}$");
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