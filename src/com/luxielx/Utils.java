package com.luxielx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public class Utils {


    public static boolean isBanned(UUID p) {
        return ConfigManager.getBanList().contains(p + "");

    }

    public static void unban(UUID p) {
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + "", null);

    }

    public static void ban(UUID p, String reason, String name) {
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".name", Bukkit.getPlayer(p).getName());
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".reason", reason);
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".by", name);
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".time", System.currentTimeMillis());
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".for", null);
    }

    public static void tempban(UUID p, String reason, String name, long time) {
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".name", Bukkit.getPlayer(p).getName());
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".reason", reason);
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".by", name);
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".time", System.currentTimeMillis());
        ConfigManager.getInstance().setData(ConfigManager.getBanList(), p + ".for", time);
    }


    public static boolean isPermanent(UUID p) {
        if (isBanned(p)) {
            return !ConfigManager.getBanList().getConfigurationSection(p + "").getKeys(true).contains("for");
        }
        return false;

    }

    public static Long getTime(UUID p) {
        if (isBanned(p)) {
            return ConfigManager.getBanList().getLong(p + ".time");
        }
        return 0l;
    }

    public static String convertToDays(long ago) {
        long seconds = ago / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String time;
        if (days <= 0) {
            time = hours % 24 + " hours " + minutes % 60 + " minutes " + seconds % 60 + " seconds";
        } else {
            time = days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes ";
        }

        return time;
    }

    public static String getReason(UUID p) {
        if (isBanned(p)) {
            return ConfigManager.getBanList().getString(p + ".reason");
        }
        return "";
    }

    public static String getBanner(UUID p) {
        if (isBanned(p)) {
            return ConfigManager.getBanList().getString(p + ".by");
        }
        return "";
    }

    public static Long getBanTime(UUID p) {
        if (isBanned(p)) {
            return ConfigManager.getBanList().getLong(p + ".for");
        }
        return 0l;
    }

    public static Long getTimeLeft(UUID p) {
        if (!isPermanent(p)) {
            return Utils.getTime(p) + Utils.getBanTime(p) - System.currentTimeMillis();
        }
        return 0l;
    }

    public static String cc(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
