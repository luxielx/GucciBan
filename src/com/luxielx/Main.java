package com.luxielx;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {
//    FileConfiguration config;
private static Permission perms = null;

    @Override
    public void onEnable() {
        ConfigManager.getInstance().setPlugin(this);
        saveDefaultConfig();
        ConfigManager.getInstance().createNewCustomConfig("banlist.yml");
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupPermissions();
        }
    }


    @Override
    public void onDisable() {

    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        // /ban {player} {reason}
        if (cmd.getName().equalsIgnoreCase("ban") && perms.has(sender,"guccibans.ban")) {
            if (args.length > 0) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    String reason = "";
                    Player p = Bukkit.getPlayer(args[0]);
                    if (args.length > 1) {
                        reason = args[1];
                    }
                    Utils.ban(p.getUniqueId(), reason, sender.getName());
                    p.kickPlayer(Utils.cc(ConfigManager.getConfig().getString("kickmessage").replaceAll("%reason%", reason).replaceAll("%staff%", sender.getName())));

                }

            }

            // unban {player}
        } else if (cmd.getName().equalsIgnoreCase("unban")&& perms.has(sender,"guccibans.unban")) {
            if (args.length > 0) {
                if (!Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) return false;
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                Utils.unban(p.getUniqueId());
            }

            //tempban {player} {duration} {reason}
            //
        } else if (cmd.getName().equalsIgnoreCase("checkban")&& perms.has(sender,"guccibans.checkban")) {
            if (args.length > 0) {
                if (!Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) return false;
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                long bantime = Utils.getTime(p.getUniqueId());
                long ago = System.currentTimeMillis() - bantime;
                String time = Utils.convertToDays(ago);
                String reason = Utils.getReason(p.getUniqueId());
                String banner = Utils.getBanner(p.getUniqueId());
                if (Utils.isPermanent(p.getUniqueId())) {
                    for (String s : ConfigManager.getConfig().getStringList("permban")) {
                        sender.sendMessage(Utils.cc(s.replaceAll("%banned%", p.getName()).replaceAll("%time%", time + "").replaceAll("%reason%", reason)
                                .replaceAll("%staff%", banner)
                        ));
                    }
                } else {
                    Long timeleft = Utils.getTimeLeft(p.getUniqueId());
                    for (String s : ConfigManager.getConfig().getStringList("tempban")) {
                        sender.sendMessage(Utils.cc(s.replaceAll("%banned%", p.getName()).replaceAll("%time%", time + "").replaceAll("%reason%", reason)
                                .replaceAll("%staff%", banner).replaceAll("%timeleft%", Utils.convertToDays(timeleft)
                                )));

                    }
                }
//                ConfigManager.getBanList().getStringList("")
            }


        } else if (cmd.getName().equalsIgnoreCase("tempban") && perms.has(sender,"guccibans.tempban")) {
            if (args.length > 1) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    String reason = "";
                    Player p = Bukkit.getPlayer(args[0]);
                    String duration = args[1];
                    int day = 0, hours = 0, minutes = 0, second = 0;

                    if (duration.contains("d")) {
                        String sec = duration.split("d")[0];
                        while (!sec.matches("-?\\d+")) {
                            sec = sec.substring(1);
                        }
                        day = Integer.valueOf(sec);
                    }
                    if (duration.contains("h")) {
                        String sec = duration.split("h")[0];
                        while (!sec.matches("-?\\d+")) {
                            sec = sec.substring(1);
                        }
                        hours = Integer.valueOf(sec);
                    }
                    if (duration.contains("m")) {
                        String sec = duration.split("m")[0];
                        while (!sec.matches("-?\\d+")) {
                            sec = sec.substring(1);
                        }
                        minutes = Integer.valueOf(sec);
                    }
                    if (duration.contains("s")) {
                        String sec = duration.split("s")[0];
                        while (!sec.matches("-?\\d+")) {
                            sec = sec.substring(1);
                        }
                        second = Integer.valueOf(sec);
                    }

                    long time = TimeUnit.DAYS.toMillis(day) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(second);

                    if (args.length > 2) {
                        reason = args[2];
                    }
                    Utils.tempban(p.getUniqueId(), reason, sender.getName(), time);
                    p.kickPlayer(Utils.cc(ConfigManager.getConfig().getString("kickmessage").replaceAll("%reason%", reason).replaceAll("%staff%", sender.getName())));


                }

            }
        }


        return false;
    }
}
