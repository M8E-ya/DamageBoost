package me.perpltxed.damageboost;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class DamageBoost extends JavaPlugin implements Listener {

    private double damageMultiplier;
    private List<Material> eligibleMaterials;

    @Override
    public void onEnable() {
        getLogger().info("Starting up the damage boost plugin");
        getServer().getPluginManager().registerEvents(new EnderPearlCooldown(this), this);

        // Load the configuration file
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        // Get the damage multiplier and eligible materials from the configuration file
        damageMultiplier = config.getDouble("damage-multiplier", 1.33);
        eligibleMaterials = config.getStringList("eligible-materials").stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!eligibleMaterials.contains(item.getType())) {
            return;
        }
        double damage = event.getDamage();
        damage *= damageMultiplier;
        event.setDamage(damage);
    }
}
