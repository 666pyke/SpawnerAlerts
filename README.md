  # SpawnerAlerts - Monitor Spawner Activity with Precision! 🧠

  ## Overview
  **SpawnerAlerts** is a Minecraft plugin designed to help server staff monitor and track mob spawner activity. Whether you're running a survival, faction, or custom gamemode, this plugin provides real-time alerts and logs when players break spawners.

  ## Features
  **Chat Alerts**: Send staff a configurable chat message when a player breaks a spawner.  
  **Discord Webhook Support**: Automatically sends detailed embeds to a Discord channel.  
  **File Logging**: Logs every spawner break with timestamp and coordinates to `logs.txt`.  
  **Permission-Based Visibility**: Only players with **spawneralerts.notify** will see chat alerts.  
  **Whitelist System**: Choose which mob types trigger alerts (e.g., **ZOMBIE**, **SKELETON**) or allow **ALL**.  
  **Reload Command**: Use **/spawneralerts reload** to reload both config and messages without restart.  
  **Async Discord Handling**: Webhooks are sent off-thread to ensure no lag on the main server.

  ## How It Works
  When a player breaks a block:
  - If it's a spawner and the mob type is **whitelisted**, the plugin will:
    - Send a message to staff (if chat is enabled)
    - Send a Discord embed (if enabled)
    - Log the action to file (if enabled)

  If the whitelist does **not** contain the spawner type (and **ALL** isn't set), the event is silently ignored.

  ## Commands
  **/spawneralerts reload** – Reloads the plugin configuration and messages.

  ## Permissions
  **spawneralerts.notify** – Allows a player to receive in-game chat alerts.  
  **spawneralerts.admin** – Allows reloading the plugin via command.

  ## Configuration

  ```yml
  spawner-alerts:
    chat: true
    discord: true
    log-to-file: true

  discord-webhook-url: "https://discord.com/api/webhooks/..."

  whitelist:
    - ZOMBIE
    - SKELETON
    - ENDERMAN
    # Use "ALL" to allow alerts for every spawner type
