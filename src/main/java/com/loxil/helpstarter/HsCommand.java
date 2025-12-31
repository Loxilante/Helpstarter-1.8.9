package com.loxil.helpstarter;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class HsCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "hs";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hs <1|2|3>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (Minecraft.getMinecraft().thePlayer == null) {
            sender.addChatMessage(new ChatComponentText("§cPlease join a world before using this command."));
            return;
        }

        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("§cError: Please provide exactly one argument. Usage: /hs <1|2|3>"));
            return;
        }

        try {
            int value = Integer.parseInt(args[0]);
            if (value >= 1 && value <= 3) {
                final int requested = value;
                // Fetch a randomized list via BotListFetcher
                sender.addChatMessage(new ChatComponentText("§eFetching available bots..."));
                
                // Run the API call on a background thread to avoid freezing the game
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<String> list = BotListFetcher.fetchList();
                            
                            if (list.isEmpty()) {
                                MinecraftUtils.addChatMessageOnMainThread("§cNo bots available.");
                                return;
                            }

                            // Print available bots
                            StringBuilder bots = new StringBuilder("§aCurrently available bots:");

                            for(int i = 0; i < list.size(); i++) {
                                bots.append(" ").append(list.get(i));
                            }

                            final String finalBots = bots.toString();
                            MinecraftUtils.addChatMessageOnMainThread(finalBots);

                            // Shuffle the list of the bots
                            // This is to distribute the requests evenly.
                            Collections.shuffle(list);

                            // Take the first X entries
                            final int count = Math.min(requested, list.size());
                            if (requested > list.size())
                            {
                                MinecraftUtils.addChatMessageOnMainThread(String.format(
                                        "§eRequested %d bots, but only %d are available. Inviting all available bots.",
                                        requested,
                                        list.size()
                                ));
                            }

                            StringBuilder command = new StringBuilder("/p");
                            
                            for (int i = 0; i < count; i++) {
                                command.append(" ").append(list.get(i));
                            }
                            
                            // Send the command
                            final String finalCommand = command.toString();
                            MinecraftUtils.sendChatMessageOnMainThread(finalCommand);
                            
                        } catch (Exception e) {
                            MinecraftUtils.addChatMessageOnMainThread("§cError: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
                
            } else {
                sender.addChatMessage(new ChatComponentText("§cError: Argument must be an integer between 1 and 3."));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cError: Argument must be an integer."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            List<String> options = new ArrayList<String>();
            options.add("1");
            options.add("2");
            options.add("3");
            return getListOfStringsMatchingLastWord(args, options);
        }
        return null;
    }
}
