package org.pronze.hypixelify.commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.pronze.hypixelify.Hypixelify;
import org.pronze.hypixelify.utils.ShopUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class BWACommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length >= 1) {
            if(sender instanceof  Player) {
                if (!sender.hasPermission("misat11.bw.admin") && !args[0].equalsIgnoreCase("gamesinv")) {
                    sender.sendMessage(ChatColor.RED + "You Don't have permissions to do this command");
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("reload")) {
             // Hypixelify.getInstance().partyTask.cancel();
             // Hypixelify.getInstance().partyManager.parties.clear();
             // Hypixelify.getInstance().playerData.clear();
             // Hypixelify.getInstance().partyManager = null;
             // Hypixelify.getInstance().playerData = null;
                Bukkit.getServer().getPluginManager().disablePlugin(Hypixelify.getInstance());
                Bukkit.getServer().getPluginManager().enablePlugin(Hypixelify.getInstance());
                sender.sendMessage("Plugin reloaded!");
                return true;
            }
            else if (args[0].equalsIgnoreCase("help"))
            {
                sender.sendMessage(ChatColor.RED + "SBAHypixelify");
                sender.sendMessage("Available commands:");
                sender.sendMessage("/bwaddon reload - Reload the addon");
                sender.sendMessage("/bwaddon help - Show available list of commands");
                sender.sendMessage("/bwaddon reset - resets all configs related to addon");
                return true;
            }
            else if(args[0].equalsIgnoreCase("reset")){
                sender.sendMessage("Resetting...");
                try{
                    Hypixelify.getConfigurator().upgradeCustomFiles();
                    ((Player)sender).performCommand("bwaddon clearnpc");
                    sender.sendMessage("Sucessfully resetted");
                } catch(Exception e){
                    e.printStackTrace();
                }

                return true;
            } else if(args[0].equalsIgnoreCase("clearnpc")){
                ShopUtil.destroyNPCFromGameWorlds();
                sender.sendMessage("Cleared all npcs from bedwars worlds");
                return true;
            } else if(args[0].equalsIgnoreCase("gamesinv")){
                if(args.length != 2){
                    sender.sendMessage("[SBAHypixelify]" + ChatColor.RED + "Unknown command, do /bwaddon help for more.");
                    return true;
                }

                if(!(sender instanceof Player)){
                    sender.sendMessage("[SBAHypixelify]" + ChatColor.RED + " You cannot do this command in the console");
                    return true;
                }

                Player player = (Player) sender;
                if(args[1].equalsIgnoreCase("solo")){
                    Hypixelify.getInstance().getGamesInventory().openForPlayer(player, 1);
                } else if(args[1].equalsIgnoreCase("double")){
                    Hypixelify.getInstance().getGamesInventory().openForPlayer(player, 2);
                } else if(args[1].equalsIgnoreCase("triple")){
                    Hypixelify.getInstance().getGamesInventory().openForPlayer(player, 3);
                }  else if(args[1].equalsIgnoreCase("squad")){
                    Hypixelify.getInstance().getGamesInventory().openForPlayer(player, 4);
                } else{
                    sender.sendMessage("[SBAHypixelify]" + ChatColor.RED + "Unknown command, do /bwaddon help for more.");
                }

                return true;
            }
            else if(!Objects.requireNonNull(Hypixelify.getConfigurator().config.getString("version")).contains(Hypixelify.getVersion())) {
                if (args[0].equalsIgnoreCase("upgrade")) {
                    try {
                        Hypixelify.getConfigurator().upgradeCustomFiles();
                        ShopUtil.destroyNPCFromGameWorlds();
                        sender.sendMessage("[SBAHypixelify]: " + ChatColor.GOLD + "Sucessfully upgraded files!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    Hypixelify.getConfigurator().config.set("version", Hypixelify.getVersion());
                    sender.sendMessage("[SBAHypixelify]: Cancelled shop and upgradeShop changes");
                    return true;
                }
            }
            else {
                sender.sendMessage("[SBAHypixelify]" + ChatColor.RED + "Unknown command, do /bwaddon help for more.");
                return true;
            }
        }
        else {
            sender.sendMessage("[SBAHypixelify]" + ChatColor.RED + "Unknown command, do /bwaddon help for more.");
            return true;
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("misat11.bw.admin"))
            return null;
        if (strings.length == 1) {
            if(!Objects.requireNonNull(Hypixelify.getConfigurator().config.getString("version")).contains(Hypixelify.getVersion())){
                return Arrays.asList("cancel","upgrade");
            }
            return Arrays.asList("reload", "help", "reset", "clearnpc", "gamesinv");
        }
        if(strings.length == 2 && strings[0].equalsIgnoreCase("gamesinv")){
            return Arrays.asList("solo", "double", "triple", "squad");
        }

        return null;
    }
}

