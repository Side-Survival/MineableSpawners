package com.dnyferguson.mineablespawners.commands;

import com.dnyferguson.mineablespawners.MineableSpawners;
import com.dnyferguson.mineablespawners.utils.Chat;
import lv.side.lang.api.LangAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MineableSpawnersCommand implements CommandExecutor {
    private final MineableSpawners plugin;
    private final GiveSubCommand giveSubCommand;
    private final SetSubCommand setSubCommand;
    private final TypesSubCommand typesSubCommand;

    public MineableSpawnersCommand(MineableSpawners plugin) {
        this.plugin = plugin;
        giveSubCommand = new GiveSubCommand();
        setSubCommand = new SetSubCommand();
        typesSubCommand = new TypesSubCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("give") && args.length == 4) {
            if (!plugin.getConfigurationHandler().getBoolean("give", "require-permission")) {
                giveSubCommand.execute(plugin, sender, args[1], args[2], args[3]);
                return true;
            }

            if (!sender.hasPermission("mineablespawners.give")) {
                plugin.getConfigurationHandler().sendMessage("give", "no-permission", sender);
                return true;
            }

            giveSubCommand.execute(plugin, sender, args[1], args[2], args[3]);
            return true;
        }

        if (subCommand.equals("set") && args.length == 2) {
            if (!plugin.getConfigurationHandler().getBoolean("set", "require-permission")) {
                setSubCommand.execute(plugin, sender, args[1]);
                return true;
            }

            if (!sender.hasPermission("mineablespawners.set")) {
                plugin.getConfigurationHandler().sendMessage("set", "no-permission", sender);
                return true;
            }

            setSubCommand.execute(plugin, sender, args[1]);
            return true;
        }

        if (subCommand.equals("types") && args.length == 1) {
            if (!plugin.getConfigurationHandler().getBoolean("types", "require-permission")) {
                typesSubCommand.execute(plugin, sender);
                return true;
            }

            if (!sender.hasPermission("mineablespawners.types")) {
                plugin.getConfigurationHandler().sendMessage("types", "no-permission", sender);
                return true;
            }

            typesSubCommand.execute(plugin, sender);
            return true;
        }

        if (subCommand.equals("reload") && sender.hasPermission("mineablespawners.reload")) {
            plugin.getConfigurationHandler().reload();
            sender.sendMessage(Chat.format("&e[MineableSpawners] &aYou have successfully reloaded the config!"));
            return true;
        }

        if (subCommand.equalsIgnoreCase("updatelang") && sender.hasPermission("mineablespawners.reload")) {
            Map<String, String> keyMap = new HashMap<>();

            if (plugin.getConfig().isConfigurationSection("mining.requirements")) {
                ConfigurationSection messages = plugin.getConfig().getConfigurationSection("mining.requirements");

                //noinspection ConstantConditions
                Collection<String> keys = messages.getKeys(true);
                for (String key : keys) {
                    if (messages.isString(key)) {
                        String value = messages.getString(key);
                        keyMap.put("s-spawners.mining.requirements." + key, value);
                    }
                }
            }

            if (plugin.getConfig().isConfigurationSection("mining.messages")) {
                ConfigurationSection messages = plugin.getConfig().getConfigurationSection("mining.messages");

                //noinspection ConstantConditions
                Collection<String> keys = messages.getKeys(true);
                for (String key : keys) {
                    if (messages.isString(key)) {
                        String value = messages.getString(key);
                        keyMap.put("s-spawners.mining." + key, value);
                    }
                }
            }

            if (plugin.getConfig().isConfigurationSection("anvil.messages")) {
                ConfigurationSection messages = plugin.getConfig().getConfigurationSection("anvil.messages");

                //noinspection ConstantConditions
                Collection<String> keys = messages.getKeys(true);
                for (String key : keys) {
                    if (messages.isString(key)) {
                        String value = messages.getString(key);
                        keyMap.put("s-spawners.anvil." + key, value);
                    }
                }
            }

            LangAPI.updateDefaults(keyMap);
            sender.sendMessage("[MineableSpawners] Default lang map reloaded!");
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        Player pSender = (sender instanceof Player player) ? player : null;

        StringBuilder msg = new StringBuilder(plugin.getConfigurationHandler().getMessage(pSender, "main", "title")).append("\n \n");
        if (!plugin.getConfigurationHandler().getBoolean("give", "require-permission")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "give"));
            msg.append("\n \n");
        }
        if (plugin.getConfigurationHandler().getBoolean("give", "require-permission") && sender.hasPermission("mineablespawners.give")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "give"));
            msg.append("\n \n");
        }
        if (!plugin.getConfigurationHandler().getBoolean("set", "require-permission")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "set"));
            msg.append("\n \n");
        }
        if (plugin.getConfigurationHandler().getBoolean("set", "require-permission") && sender.hasPermission("mineablespawners.set")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "set"));
            msg.append("\n \n");
        }
        if (!plugin.getConfigurationHandler().getBoolean("types", "require-permission")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "types"));
            msg.append("\n \n");
        }
        if (plugin.getConfigurationHandler().getBoolean("types", "require-permission") && sender.hasPermission("mineablespawners.types")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "types"));
            msg.append("\n \n");
        }
        if (sender.hasPermission("mineablespawners.reload")) {
            msg.append(plugin.getConfigurationHandler().getMessage(pSender, "main", "reload"));
        }
        sender.sendMessage(msg.toString());
    }
}
