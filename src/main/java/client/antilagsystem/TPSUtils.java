package client.antilagsystem;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class TPSUtils {

    public static int getTPS() {
        try {
            Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            Field tpsField = server.getClass().getField("recentTps");
            double[] tps = (double[]) tpsField.get(server);
            return (int) Math.round(tps[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}