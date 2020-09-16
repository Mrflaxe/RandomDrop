package ru.mrflaxe.randomdrop.listeners;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
		PlayerInventory inv = p.getInventory();
		List<ItemStack> drops = e.getDrops();
		
		List<String> worlds = config.getList("worlds");
		if(!worlds.contains(p.getWorld().getName())) return;
		
		List<ItemStack> drop = drops.parallelStream().filter(i -> isFall()).collect(Collectors.toList());
		List<ItemStack> loot = drops.parallelStream().filter(i -> !drop.contains(i)).collect(Collectors.toList());
		drops.clear();

		Inventory storage = Bukkit.createInventory(null, 54);
		System.out.println(loot.size());
		loot.stream().forEach(i -> storage.addItem(i));
		
		Location loc = p.getLocation();
		drop.stream().peek(i -> loc.getWorld().dropItemNaturally(loc, i))
				.forEach(i -> inv.clear(inv.first(i)));
		
		Bukkit.getScheduler().runTaskLater(plugin, task -> {
			p.openInventory(storage);
			messages.getAndSend(p, "attention");
		}, 20);
	}
	
	private boolean isFall() {
		Random random = new Random();
		int chance = config.getInt("chance");
		return random.nextInt(100) <= chance;
	}
	
	public void register(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
