package client.antilagsystem.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import client.antilagsystem.AntiLag_System;

public class EntityCleanupTask extends BukkitRunnable {

    private final AntiLag_System plugin;

    public EntityCleanupTask(AntiLag_System plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Verificar si la limpieza de entidades está habilitada en la configuración
        if (!plugin.getConfigManager().getConfig().getBoolean("entityCleanupEnabled", false)) {
            return;
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (shouldRemoveEntity(entity)) {
                    entity.remove();
                }
            }
        }
    }

    private boolean shouldRemoveEntity(Entity entity) {
        EntityType type = entity.getType();
        // Check for entities to exclude from removal
        return type != EntityType.BOAT &&
                type != EntityType.ITEM_FRAME &&
                type != EntityType.ENDER_CRYSTAL &&
                type != EntityType.MINECART &&
                !type.name().contains("SIGN"); // Covers all types of signs
    }
}
