package com.dnyferguson.mineablespawners.commands;

import com.cryptomorin.xseries.XMaterial;
import com.dnyferguson.mineablespawners.MineableSpawners;
import com.dnyferguson.mineablespawners.utils.Chat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SetSubCommand {
    private final Set<Material> invisibleBlocks = new HashSet<>();

    public SetSubCommand() {
        invisibleBlocks.add(XMaterial.AIR.parseMaterial());
    }

    public void execute(MineableSpawners plugin, CommandSender sender, String type) {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("Only players can run this command!");
            return;
        }

        Player player = (Player) sender;

        if (plugin.getConfigurationHandler().getList("set", "blacklisted-worlds").contains(player.getWorld().getName())) {
            player.sendMessage(plugin.getConfigurationHandler().getMessage(null, "set", "blacklisted"));
            return;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(plugin.getConfigurationHandler().getMessage(null, "set", "invalid-type"));
            return;
        }

        if (plugin.getConfigurationHandler().getBoolean("set", "require-individual-permission")) {
            if (!player.hasPermission("mineablespawners.set." + type.toLowerCase())) {
                player.sendMessage(plugin.getConfigurationHandler().getMessage(null, "set", "no-individual-permission"));
                return;
            }
        }

        Block target = player.getTargetBlock(invisibleBlocks, 5);

        if (target.getState().getBlock().getType() != XMaterial.SPAWNER.parseMaterial()) {
            player.sendMessage(plugin.getConfigurationHandler().getMessage(null, "set", "not-looking-at"));
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) target.getState();

        String from = Chat.uppercaseStartingLetters(spawner.getSpawnedType().name());
        String to = Chat.uppercaseStartingLetters(type);
        if (from.equals(to)) {
            player.sendMessage(plugin.getConfigurationHandler().getMessage(null, "set", "already-type"));
            return;
        }

        spawner.setSpawnedType(entityType);
        spawner.update();

        player.sendMessage(plugin.getConfigurationHandler().getMessage(null, "set", "success").replace("%from%", from).replace("%to%", to));
    }
}
