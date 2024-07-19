package client.antilagsystem.commands;

import client.antilagsystem.AntiLag_System;
import client.antilagsystem.TPSUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LagCommand implements CommandExecutor {
    private final AntiLag_System plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> pendingConfirmations = new HashMap<>();
    private static final long COOLDOWN_TIME = 60000; // 1 minuto en milisegundos
    private static final long CONFIRMATION_TIMEOUT = 30000; // 30 segundos para confirmar

    public LagCommand(AntiLag_System plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase("confirmar")) {
            if (pendingConfirmations.containsKey(player.getUniqueId())) {
                long timeElapsed = System.currentTimeMillis() - pendingConfirmations.get(player.getUniqueId());
                if (timeElapsed < CONFIRMATION_TIMEOUT) {
                    pendingConfirmations.remove(player.getUniqueId());
                    executeLagCommand(player);
                } else {
                    pendingConfirmations.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "La confirmación ha expirado. Por favor, ejecuta el comando de nuevo.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "No hay ninguna acción pendiente de confirmación.");
            }
            return true;
        }

        if (!checkCooldown(player)) {
            player.sendMessage(ChatColor.RED + "Debes esperar antes de usar este comando de nuevo.");
            return true;
        }

        pendingConfirmations.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(ChatColor.YELLOW + "Estás a punto de optimizar el rendimiento del servidor. Por favor, confirma ejecutando " + ChatColor.GREEN + "/lag confirmar" + ChatColor.YELLOW + " en los próximos 30 segundos.");
        return true;
    }

    private boolean checkCooldown(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeElapsed = System.currentTimeMillis() - cooldowns.get(player.getUniqueId());
            if (timeElapsed < COOLDOWN_TIME) {
                return false;
            }
        }
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }

    private void executeLagCommand(Player player) {
        player.sendMessage(ChatColor.GREEN + "Verificando el lag del servidor...");

        showServerStatus(player);
        Map<String, Integer> optimizedChunks = optimizeChunks();
        optimizeServerPerformance();

        for (Map.Entry<String, Integer> entry : optimizedChunks.entrySet()) {
            player.sendMessage(ChatColor.GREEN + "Mundo: " + ChatColor.YELLOW + entry.getKey() + ChatColor.GREEN + ", Chunks optimizados: " + ChatColor.YELLOW + entry.getValue());
        }

        player.sendMessage(ChatColor.GREEN + "¡Optimización completada!");
    }

    private void showServerStatus(Player player) {
        double tps = TPSUtils.getTPS();
        int ping = getPlayerPing(player);

        player.sendMessage(ChatColor.YELLOW + "TPS: " + ChatColor.WHITE + String.format("%.2f", tps));
        player.sendMessage(ChatColor.YELLOW + "Ping: " + ChatColor.WHITE + ping + "ms");
    }

    private int getPlayerPing(Player player) {
        return player.getPing();
    }

    private Map<String, Integer> optimizeChunks() {
        Map<String, Integer> optimizedChunks = new HashMap<>();

        for (World world : plugin.getServer().getWorlds()) {
            int count = 0;
            for (Chunk chunk : world.getLoadedChunks()) {
                if (world.getPlayers().isEmpty() && !world.getName().toLowerCase().contains("nether")) {
                    chunk.unload(true);
                    count++;
                } else {
                    chunk.load();
                }
            }
            optimizedChunks.put(world.getName(), count);
        }

        return optimizedChunks;
    }

    private void optimizeServerPerformance() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : plugin.getServer().getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (!(entity instanceof Player) && entity.getTicksLived() > 12000) {
                            entity.remove();
                        }
                    }
                }
                System.gc();
            }
        }.runTaskTimer(plugin, 0L, 6000L); // Ejecutar cada 5 minutos
    }
}
