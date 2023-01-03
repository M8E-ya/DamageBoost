package me.perpltxed.damageboost;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageBoost extends JavaPlugin implements Listener, CommandExecutor {

    private boolean enabled;
    private Map<Material, Double> boostFactors;
    private double boostValue;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("toggleboost").setExecutor(this);

        enabled = true;
        boostFactors = new HashMap<>();
        boostValue = getConfig().getDouble("damageBoost.value");

        for (String material : getConfig().getConfigurationSection("damageBoost.materials").getKeys(false)) {
            Material mat = Material.valueOf(material);
            double boostFactor = getConfig().getDouble("damageBoost.materials." + material);
            boostFactors.put(mat, boostFactor);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!enabled || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();

        Double boostFactor = boostFactors.get(item.getType());
        if (boostFactor == null) {
            return;
        }

        double damage = event.getDamage();
        damage *= boostFactor * boostValue;
        event.setDamage(damage);

        // player.sendMessage(ChatColor.GREEN + "Your " + item.getType().name().toLowerCase().replace("_", " ") + " dealt an extra " + (int)(boostFactor * boostValue * 100) + "% damage!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (label.equalsIgnoreCase("toggleboost")) {
            enabled = !enabled;
            sender.sendMessage(ChatColor.GREEN + "Damage boost " + (enabled ? "enabled" : "disabled") + ".");
            return true;
        }

        return false;
    }
}

