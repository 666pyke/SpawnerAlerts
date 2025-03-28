package org.me.pyke.spawneralerts.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.me.pyke.spawneralerts.SpawnerAlerts;
import org.me.pyke.spawneralerts.managers.DiscordWebhookManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Listener class that handles the event when a player breaks a mob spawner.
 * Sends alerts via chat, Discord webhook, and optionally logs the event to a file.
 */
public class SpawnerBreakListener implements Listener {

    private final SpawnerAlerts plugin;
    private FileConfiguration messages;
    private final DiscordWebhookManager webhookManager; // Use the existing webhook manager

    public SpawnerBreakListener(SpawnerAlerts plugin) {
        this.plugin = plugin;
        this.webhookManager = plugin.getDiscordWebhookManager(); // Get the existing webhook manager
    }

    /**
     * Handles spawner break events.
     * Only processes if the event is not cancelled and the block is a spawner.
     */
    @EventHandler(ignoreCancelled = true)
    public void onSpawnerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();

        // Extract spawner information
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        String mobType = Objects.requireNonNull(spawner.getSpawnedType()).name();
        String world = block.getWorld().getName();
        int x = block.getX(), y = block.getY(), z = block.getZ();

        // Check if this mobType is allowed by the whitelist
        List<String> whitelist = plugin.getConfig().getStringList("whitelist");
        if (!whitelist.contains("ALL") && !whitelist.contains(mobType)) return;

        // Send chat message to staff with permission
        if (plugin.getConfig().getBoolean("spawner-alerts.chat", true)) {
            String rawMessage = plugin.getMessagesConfig().getString("spawner-break.chat-message",
                    "&c%player% &7broke a &e%mob% spawner&7!");
            String parsed = ChatColor.translateAlternateColorCodes('&',
                    rawMessage.replace("%player%", player.getName()).replace("%mob%", mobType));

            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("spawneralerts.notify"))
                    .forEach(p -> p.sendMessage(parsed));
        }

        // Send Discord embed via webhook
        if (webhookManager.isDiscordEnabled()) {
            String title = plugin.getMessagesConfig().getString("spawner-break.discord.title", "Spawner Broken!");
            String desc = plugin.getMessagesConfig().getString("spawner-break.discord.description",
                            "**%player%** broke a **%mob%** spawner in **%world%** at **X: %x%, Y: %y%, Z: %z%**.")
                    .replace("%player%", player.getName())
                    .replace("%mob%", mobType)
                    .replace("%world%", world)
                    .replace("%x%", String.valueOf(x))
                    .replace("%y%", String.valueOf(y))
                    .replace("%z%", String.valueOf(z));

            int color = plugin.getMessagesConfig().getInt("spawner-break.discord.color", 14423100);
            String avatarUrl = "https://minotar.net/avatar/" + player.getName();

            String json = "{ \"embeds\": [ {"
                    + "\"title\": \"" + title + "\","
                    + "\"description\": \"" + desc + "\","
                    + "\"color\": " + color + ","
                    + "\"footer\": { \"text\": \"SpawnerAlerts - 666pyke\" },"
                    + "\"thumbnail\": { \"url\": \"" + avatarUrl + "\" }"
                    + "} ] }";

            webhookManager.sendEmbed(json);
        }

        // Write to logs.txt if enabled
        if (plugin.getConfig().getBoolean("spawner-alerts.log-to-file", true)) {
            logSpawnerBreak(player.getName(), mobType, world, x, y, z);
        }

    }

    /**
     * Logs the spawner break to logs.txt using a configurable format.
     */
    private void logSpawnerBreak(String player, String mobType, String world, int x, int y, int z) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logFormat = messages.getString("log-format",
                "[%time%] %player% broke a %mob% spawner in %world% at (%x%, %y%, %z%)");

        String line = logFormat
                .replace("%time%", time)
                .replace("%player%", player)
                .replace("%mob%", mobType)
                .replace("%world%", world)
                .replace("%x%", String.valueOf(x))
                .replace("%y%", String.valueOf(y))
                .replace("%z%", String.valueOf(z));

        File logFile = new File(plugin.getDataFolder(), "logs.txt");

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(line + "\n");
        } catch (IOException e) {
            plugin.getLogger().warning("Error writing to logs.txt: " + e.getMessage());
        }
    }

}
