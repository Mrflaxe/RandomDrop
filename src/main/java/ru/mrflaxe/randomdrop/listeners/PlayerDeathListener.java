package ru.mrflaxe.randomdrop.listeners;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ru.soknight.lib.configuration.Configuration;
import ru.soknight.lib.configuration.Messages;

public class PlayerDeathListener implements Listener{
	
	private final Configuration config;
	private final Messages messages;
	
	private final JavaPlugin plugin;
	
	public PlayerDeathListener(Configuration config,Messages messages, JavaPlugin plugin) {
		this.config = config;
		this.messages = messages;
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void PlayerDeathevent(PlayerDeathEvent e) {
		Player p = e.getEntity();
		List<ItemStack> drops = e.getDrops();
		
		// filtering worlds by dint of config.
		List<String> worlds = config.getList("worlds");
		if(worlds == null || !worlds.contains(p.getWorld().getName())) return;
		
		e.setKeepInventory(true);
		drops = filter(drops);
		
		List<ItemStack> drop = drops.parallelStream().filter(i -> isFall()).collect(Collectors.toList());
		List<ItemStack> loot = drops.parallelStream().filter(i -> !drop.contains(i)).collect(Collectors.toList());
		drops.clear();
		
		// if loot is empty we will not open empty inventory, but we send a message about it.
		if(loot.size() == 0) {
			Bukkit.getScheduler().runTaskLater(plugin, task -> {
				messages.getColoredList("messages.empty-loot").stream()
						.forEach(s -> p.sendMessage(s));
			}, 20);
			return;
		}
		
		// creating the empty Inventory
		Inventory storage = Bukkit.createInventory(null, 54);
		// filling the inventory with the player's remaining loot
		loot.stream().forEach(i -> storage.addItem(i));
		
		// open this inventory for a player.
		Bukkit.getScheduler().runTaskLater(plugin, task -> {
			p.openInventory(storage);
			messages.getAndSend(p, "messages.attention");
		}, 20);
	}
	
	// fast randomizer
	private boolean isFall() {
		Random random = new Random();
		int chance = config.getInt("chance");
		return random.nextInt(100) <= chance;
	}
	
	// just filter for my other plugin
	private List<ItemStack> filter(List<ItemStack> drops) {
		List<ItemStack> blackList = drops.parallelStream()
				.filter(i -> (i.getType() == Material.SUNFLOWER))
				.filter(i -> i.getItemMeta().hasEnchant(Enchantment.DURABILITY))
				.collect(Collectors.toList());
		
		blackList.parallelStream().forEach(i -> drops.remove(i));
		return drops;
	}
	
	// registration of our handler which will perform in the main class.
	public void register() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
