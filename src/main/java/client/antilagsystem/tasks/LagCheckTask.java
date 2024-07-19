package client.antilagsystem.tasks;

import client.antilagsystem.AntiLag_System;
import client.antilagsystem.TPSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LagCheckTask extends BukkitRunnable {

    private final AntiLag_System plugin;

    public LagCheckTask(AntiLag_System plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double tps = TPSUtils.getTPS();
        if (tps == -1) {
            plugin.getLogger().severe("Error al recuperar TPS.");
            return;
        }
        plugin.getLogger().info("TPS actual: " + tps);

        if (tps < 18.0) {
            plugin.getLogger().warning("¡Retraso del servidor detectado! TPS: " + tps);
            notifyOps(tps);
        }
    }

    private void notifyOps(double tps) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage("§c¡Retraso del servidor detectado! TPS: " + tps);
            }
        }
    }
}
