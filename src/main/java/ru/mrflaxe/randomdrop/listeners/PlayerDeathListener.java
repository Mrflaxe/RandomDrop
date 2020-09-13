package ru.mrflaxe.randomdrop.listeners;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import ru.soknight.lib.configuration.Configuration;

public class PlayerDeathListener implements Listener{
	
	private final Configuration config;
	
	public PlayerDeathListener(Configuration config) {
		this.config = config;
	}
	
	@EventHandler
	public void PlayerDeathevent(PlayerDeathEvent e) {
		Player p = e.getEntity();
		PlayerInventory inv = p.getInventory();
		List<ItemStack> drops = e.getDrops();
		
		e.setKeepInventory(true);
		List<ItemStack> loot = drops.parallelStream().filter(i -> isFall()).collect(Collectors.toList());
		drops.clear();

		Location loc = p.getLocation();
		System.out.println("loot.size = " + loot.size());
		loot.stream().peek(i -> loc.getWorld().dropItemNaturally(loc, i))
		.forEach(i -> {
			int f = inv.first(i);
			inv.clear(f);
		});
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
