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
        return "/hs <1|2|3|a|q|available|query|help> [username]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (Minecraft.getMinecraft().thePlayer == null) {
            MinecraftUtils.addChatMessageOnMainThread("§4Error: Please join a world before using this command.");
            return;
        }

        if (args.length < 1 || args.length > 2) {
            MinecraftUtils.addChatMessageOnMainThread("§4Error: Invalid number of arguments. Use §c/hs help§4 for usage information.");
            return;
        }

        String arg = args[0].toLowerCase();

        // Handle 'help' command
        if (arg.equals("help")) {
            MinecraftUtils.addChatMessageOnMainThread("§6=== HelpStarter Commands ===");
            MinecraftUtils.addChatMessageOnMainThread("§e/hs <1|2|3> §7- Invite 1, 2, or 3 bots to your party");
            MinecraftUtils.addChatMessageOnMainThread("§e/hs a|available [optional:username] §7- Show available bots");
            MinecraftUtils.addChatMessageOnMainThread("§e/hs q|query [optional:username] §7- Show all bots");
            MinecraftUtils.addChatMessageOnMainThread("§e/hs help §7- Show this help message");
            MinecraftUtils.addChatMessageOnMainThread("§bDiscord: §floxilante");
            return;
        }

        // Handle 'a' or 'available' command - show available bots
        if (arg.equals("a") || arg.equals("available")) {
            final String username = args.length >= 2 ? args[1] : MinecraftUtils.getUsername();

            if (!MinecraftUtils.isValidUsername(username)) {
                MinecraftUtils.addChatMessageOnMainThread("§4Error: Invalid username.");
                return;
            }
            
            MinecraftUtils.addChatMessageOnMainThread("§eFetching available bots for " + username + "...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<String> list = BotListFetcher.fetchListFromApi(username);

                        if (list.isEmpty()) {
                            MinecraftUtils.addChatMessageOnMainThread("§cNo bots available for " + username + ".");
                            return;
                        }

                        StringBuilder bots = new StringBuilder("§a");
                        for (int i = 0; i < list.size(); i++) {
                            bots.append(list.get(i)).append(' ');
                        }
                        MinecraftUtils.addChatMessageOnMainThread(bots.toString());

                    } catch (Exception e) {
                        MinecraftUtils.addChatMessageOnMainThread("§4Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
            return;
        }

        // Handle 'q' or 'query' command - show all bots classified by availability
        if (arg.equals("q") || arg.equals("query")) {
            final String username = args.length >= 2 ? args[1] : MinecraftUtils.getUsername();

            if (!MinecraftUtils.isValidUsername(username)) {
                MinecraftUtils.addChatMessageOnMainThread("§4Error: Invalid username.");
                return;
            }
            
            MinecraftUtils.addChatMessageOnMainThread("§eQuerying bot status for " + username + "...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BotListFetcher.BotClassification classification = BotListFetcher.fetchAllBotsClassified(username);
                        
                        int total = classification.available.size() + classification.busy.size() + classification.unavailable.size();
                        MinecraftUtils.addChatMessageOnMainThread("§6=== Bot Status for " + username + " ===");
                        MinecraftUtils.addChatMessageOnMainThread("§aTotal bots: §f" + total);
                        
                        // Available bots
                        if (!classification.available.isEmpty()) {
                            StringBuilder bots = new StringBuilder("§aAvailable (" + classification.available.size() + "):");
                            for (String bot : classification.available) {
                                bots.append(" ").append(bot);
                            }
                            MinecraftUtils.addChatMessageOnMainThread(bots.toString());
                        } else {
                            MinecraftUtils.addChatMessageOnMainThread("§aAvailable (0): §7None");
                        }
                        
                        // Busy bots
                        if (!classification.busy.isEmpty()) {
                            StringBuilder bots = new StringBuilder("§eBusy (" + classification.busy.size() + "):");
                            for (String bot : classification.busy) {
                                bots.append(" ").append(bot);
                            }
                            MinecraftUtils.addChatMessageOnMainThread(bots.toString());
                        } else {
                            MinecraftUtils.addChatMessageOnMainThread("§eBusy (0): §7None");
                        }
                        
                        // Unavailable bots
                        if (!classification.unavailable.isEmpty()) {
                            StringBuilder bots = new StringBuilder("§cUnavailable (" + classification.unavailable.size() + "):");
                            for (String bot : classification.unavailable) {
                                bots.append(" ").append(bot);
                            }
                            MinecraftUtils.addChatMessageOnMainThread(bots.toString());
                        } else {
                            MinecraftUtils.addChatMessageOnMainThread("§cUnavailable (0): §7None");
                        }
                        
                    } catch (Exception e) {
                        MinecraftUtils.addChatMessageOnMainThread("§4Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
            return;
        }

        // Handle numeric arguments (1, 2, 3)
        try {
            int value = Integer.parseInt(args[0]);
            if (value >= 1 && value <= 3) {
                final int requested = value;
                // Fetch a randomized list via BotListFetcher
                MinecraftUtils.addChatMessageOnMainThread("§eFetching available bots...");
                
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
                            MinecraftUtils.addChatMessageOnMainThread(bots.toString());

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
                            MinecraftUtils.sendChatMessageOnMainThread(command.toString());
                            
                        } catch (Exception e) {
                            MinecraftUtils.addChatMessageOnMainThread("§4Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
                
            } else {
                MinecraftUtils.addChatMessageOnMainThread("§4Error: Numeric argument must be between 1 and 3.");
            }
        } catch (NumberFormatException e) {
            MinecraftUtils.addChatMessageOnMainThread("§4Error: Invalid argument. Use §c/hs help§4 for usage information.");
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
            options.add("a");
            options.add("q");
            options.add("available");
            options.add("query");
            options.add("help");
            return getListOfStringsMatchingLastWord(args, options);
        }

        // For 'a', 'q', 'available', 'query' commands, provide player name completion for the second argument
        if (args.length == 2) {
            String firstArg = args[0].toLowerCase();
            if (firstArg.equals("a") || firstArg.equals("q") || firstArg.equals("available") || firstArg.equals("query")) {
                return getListOfStringsMatchingLastWord(args, MinecraftUtils.getOnlinePlayerNames());
            }
        }
        return null;
    }
}
