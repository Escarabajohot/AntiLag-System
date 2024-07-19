package client.antilagsystem;

import client.antilagsystem.commands.LagCommand;
import client.antilagsystem.events.CustomEventListener;
import client.antilagsystem.tasks.EntityCleanupTask;
import client.antilagsystem.tasks.LagCheckTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class AntiLag_System extends JavaPlugin {

    private BukkitTask lagCheckTask;
    private BukkitTask entityCleanupTask;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("El plugin AntiLag System ha sido activado");
        getLogger().info("AntiLag System: Un plugin diseñado para reducir el lag en el servidor, mejorando el rendimiento y la experiencia del usuario.");
        getLogger().info("");
        getLogger().info("Tipo de plugin: Privado");
        getLogger().info("Desarollador por: AntilagServers Studios");

        getServer().getPluginManager().registerEvents(new CustomEventListener(), this);

        // Initialize the config manager
        configManager = new ConfigManager(this);

        // Register commands
        this.getCommand("lag").setExecutor(new LagCommand(this));

        // Start the lag check task
        startLagCheckTask();

        // Check if entity cleanup is enabled in config
        if (configManager.getConfig().getBoolean("entityCleanupEnabled", false)) {
            startEntityCleanupTask();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("El plugin AntiLag System ha sido desactivado");
        getLogger().info("AntiLag System: El plugin diseñado para reducir el lag en el servidor ha sido desactivado. El rendimiento y la experiencia del usuario podrían verse afectados.");

        // Cancel the lag check task if running
        if (lagCheckTask != null) {
            lagCheckTask.cancel();
        }

        // Cancel the entity cleanup task if running
        if (entityCleanupTask != null) {
            entityCleanupTask.cancel();
        }
    }

    private void startLagCheckTask() {
        long interval = configManager.getConfig().getLong("lagCheckInterval", 6000L); // default to 5 minutes
        lagCheckTask = new LagCheckTask(this).runTaskTimer(this, 0, interval);
    }

    private void startEntityCleanupTask() {
        long interval = configManager.getConfig().getLong("entityCleanupInterval", 12000L); // default to 10 minutes
        entityCleanupTask = new EntityCleanupTask(this).runTaskTimer(this, 0, interval);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
