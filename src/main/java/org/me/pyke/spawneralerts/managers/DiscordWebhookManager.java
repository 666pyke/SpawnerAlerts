package org.me.pyke.spawneralerts.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Handles sending Discord webhook messages asynchronously.
 */
public class DiscordWebhookManager {

    private final JavaPlugin plugin;
    private boolean discordEnabled;
    private String webhookUrl;

    public DiscordWebhookManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadWebhookSettings(); // Load settings on startup
    }

    /**
     * Reloads the Discord settings from config.
     */
    public void reloadWebhookSettings() {
        FileConfiguration config = plugin.getConfig();
        this.discordEnabled = config.getBoolean("spawner-alerts.discord", false);
        this.webhookUrl = config.getString("discord-webhook-url", "");

        // Log new settings for debugging
        plugin.getLogger().info("Discord alerts enabled: " + discordEnabled);
        plugin.getLogger().info("Webhook URL: " + (webhookUrl.isEmpty() ? "Not set" : "Loaded successfully"));
    }

    /**
     * Sends a Discord embed message asynchronously if Discord alerts are enabled.
     */
    public void sendEmbed(String jsonPayload) {
        if (!discordEnabled) {
            plugin.getLogger().info("Skipping webhook: Discord alerts are disabled in config.");
            return;
        }

        if (webhookUrl == null || webhookUrl.isEmpty()) {
            plugin.getLogger().warning("Webhook URL is missing or empty.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 204) {
                    plugin.getLogger().warning("Webhook failed. HTTP response code: " + responseCode);
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Exception while sending webhook: " + e.getMessage());
            }
        });
    }

    /**
     * Checks if Discord alerts are enabled.
     */
    public boolean isDiscordEnabled() {
        return discordEnabled;
    }


}
