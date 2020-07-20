package org.pronze.hypixelify.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.pronze.hypixelify.Hypixelify;
import org.pronze.hypixelify.Party.Party;
import org.pronze.hypixelify.database.PlayerDatabase;
import org.pronze.hypixelify.utils.ShopUtil;

import java.util.Arrays;
import java.util.List;

public class PartyCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player))
            return true;

        if(!Hypixelify.getConfigurator().config.getBoolean("party.enabled", true)){
            sender.sendMessage("Cannot access command, party system is disabled.");
        }
        Player player = (Player) sender;

        if(args[0].equalsIgnoreCase("invite")){
            if (args.length == 3){
                Player invited = Bukkit.getPlayerExact(args[1]);
                if (invited == null) {
                    player.sendMessage("§cCould not find player!, check the username and make sure he's online!");
                    return true;
                }

                PlayerDatabase data = Hypixelify.getInstance().playerData.get(player.getUniqueId());
                if(data == null)
                    Hypixelify.getInstance().playerData.put(player.getUniqueId(), new PlayerDatabase(player));

                PlayerDatabase invitedData = Hypixelify.getInstance().playerData.get(player.getUniqueId());
                if(invitedData == null)
                    Hypixelify.getInstance().playerData.put(invited.getUniqueId(), new PlayerDatabase(invited));

                Party party = data.getParty();
                if(party == null){
                    party = new Party(player);
                }
                if(invitedData.isInParty() ||(!party.canAnyoneInvite() && !player.equals(party.getLeader()))){
                    for(String message : Hypixelify.getConfigurator().config.getStringList("party.message.cannotinvite")){
                        player.sendMessage(ShopUtil.translateColors(message));
                    }
                    return true;
                }

                if(invitedData.isInvited()){
                    for(String message : Hypixelify.getConfigurator().config.getStringList("party.message.alreadyInvited")){
                        player.sendMessage(ShopUtil.translateColors(message));
                    }
                    return true;
                }

                if(party.canAnyoneInvite() || player.equals(party.getLeader())){
                    Hypixelify.getInstance().playerData.get(invited.getUniqueId()).setInvited(true);
                    Hypixelify.getInstance().playerData.get(invited.getUniqueId()).setInvitedParty(party);
                    for(String message : Hypixelify.getConfigurator().config.getStringList("party.message.invite")){
                        invited.sendMessage(ShopUtil.translateColors(message).replace("{player}", player.getDisplayName()));
                    }
                    return true;
                }

            }

        } else if(args[0].equalsIgnoreCase("accept") &&
                Hypixelify.getInstance().playerData.get(player.getUniqueId()) != null && Hypixelify.getInstance().playerData.get(player.getUniqueId()).isInvited()){
            Hypixelify.getInstance().playerData.get(player.getUniqueId()).setInvited(false);

            PlayerDatabase data = Hypixelify.getInstance().playerData.get(player.getUniqueId());
            if(!data.isInParty()){
                Hypixelify.getInstance().playerData.get(player.getUniqueId()).getInvitedParty().addMember(player);
                Hypixelify.getInstance().playerData.get(player.getUniqueId()).setParty(Hypixelify.getInstance().playerData.get(player.getUniqueId()).getInvitedParty());
                Hypixelify.getInstance().playerData.get(player.getUniqueId()).setInvited(false);
                Hypixelify.getInstance().playerData.get(player.getUniqueId()).setIsInParty(true);

                for(Player p : Hypixelify.getInstance().playerData.get(player.getUniqueId()).getParty().getAllPlayers()) {
                    for (String message : Hypixelify.getConfigurator().config.getStringList("party.message.accepted")) {
                        p.sendMessage(ShopUtil.translateColors(message).replace("{player}", player.getDisplayName()));
                    }
                }
                return true;
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return null;
        Player player = (Player) commandSender;

        if(Hypixelify.getInstance().playerData.get(player) != null && Hypixelify.getInstance().playerData.get(player).isInvited()){
            return Arrays.asList("accept", "decline");
        }

        return Arrays.asList("invite");
    }
}