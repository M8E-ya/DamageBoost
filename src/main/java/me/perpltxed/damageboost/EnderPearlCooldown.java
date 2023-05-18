package me.perpltxed.damageboost;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlCooldown implements Listener {

    private final DamageBoost plugin;
    private final Map<UUID, Long> pearlCooldowns;

    public EnderPearlCooldown(DamageBoost plugin) {
        this.plugin = plugin;
        pearlCooldowns = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (isOnCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage("Ender pearl is on cooldown!");
        } else {
            startCooldown(player);
        }
    }


    private boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (pearlCooldowns.containsKey(playerId)) {
            long cooldownEnd = pearlCooldowns.get(playerId);
            long currentTime = System.currentTimeMillis();
            return currentTime < cooldownEnd;
        }
        return false;
    }

    private void startCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownTime = plugin.getConfig().getLong("ender-pearl-cooldown", 5000); // Default cooldown: 5 seconds
        long cooldownEnd = currentTime + cooldownTime;
        pearlCooldowns.put(playerId, cooldownEnd);
    }
}
