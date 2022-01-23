package com.luxielx;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class JoinEvent implements Listener {

    @EventHandler
    public void join(AsyncPlayerPreLoginEvent e) {
        UUID p = e.getUniqueId();
        boolean banned = false;
        if (Utils.isBanned(p)) {
            if (Utils.isPermanent(p)) {
                banned = true;
            } else {
                if (Utils.getTimeLeft(p) > 0) banned = true;
            }

        }
        if (banned) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
    }
}
