package org.me.pyke.spawneralerts.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.me.pyke.spawneralerts.SpawnerAlerts;

import java.io.File;

/**
 * Command handler for /spawneralerts
 * Currently supports reloading config and messages.
 */
public class SpawnerAlertsCommand implements CommandExecutor {

    private final SpawnerAlerts plugin;

    public SpawnerAlertsCommand(SpawnerAlerts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();

            // Check if sender has the required permission
            if (!sender.hasPermission("spawneralerts.admin")) {
                return true;
            }

            // Reload messages.yml
            File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
            plugin.setMessagesConfig(messagesConfig);

            // Reload Webhook settings
            plugin.getDiscordWebhookManager().reloadWebhookSettings();

            // Get reload message from messages.yml
            String reloadMessage = plugin.getMessagesConfig().getString("reload-message",
                    "&a✅ SpawnerAlerts config, messages, and webhook settings reloaded!");

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', reloadMessage));
            return true;
        }

        // If no arguments or unknown subcommand, show help message from messages.yml
        FileConfiguration messages = plugin.getMessagesConfig();
        if (messages.contains("command-help")) {
            for (String line : messages.getStringList("command-help")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        } else {
            // Fallback in case messages.yml is missing the entry
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " reload");
        }

        return true;
    }
}
