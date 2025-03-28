package org.me.pyke.spawneralerts;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.me.pyke.spawneralerts.commands.SpawnerAlertsCommand;
import org.me.pyke.spawneralerts.listeners.SpawnerBreakListener;
import org.me.pyke.spawneralerts.managers.DiscordWebhookManager;

import java.io.File;
import java.util.Objects;

/**
 * Main class for the SpawnerAlerts plugin.
 * Handles plugin lifecycle and loads configuration files.
 */
public final class SpawnerAlerts extends JavaPlugin {

    private FileConfiguration messagesConfig;

    private DiscordWebhookManager discordWebhookManager;


    @Override
    public void onEnable() {
        // Save config.yml if it doesn't exist
        saveDefaultConfig();

        // Save messages.yml if it doesn't exist
        saveMessagesFile();

        // Load messages.yml into memory
        loadMessagesFile();

        discordWebhookManager = new DiscordWebhookManager(this); // Initialize webhook manager

        // Register spawner break event listener
        getServer().getPluginManager().registerEvents(
                new SpawnerBreakListener(this), this
        );

        // Register command executor for /spawneralerts
        Objects.requireNonNull(getCommand("spawneralerts")).setExecutor(new SpawnerAlertsCommand(this));

        getLogger().info("SpawnerAlerts has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpawnerAlerts is going to sleeeep... \uD83D\uDE34");
    }

    /**
     * Saves messages.yml from resources if it doesn't already exist.
     */
    private void saveMessagesFile() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    /**
     * Loads messages.yml into memory for access throughout the plugin.
     */
    private void loadMessagesFile() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Returns the messages configuration.
     */
    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    /**
     * Sets a new messages configuration, used for reloads.
     */
    public void setMessagesConfig(FileConfiguration config) {
        this.messagesConfig = config;
    }

    public DiscordWebhookManager getDiscordWebhookManager() {
        return discordWebhookManager;
    }

}
