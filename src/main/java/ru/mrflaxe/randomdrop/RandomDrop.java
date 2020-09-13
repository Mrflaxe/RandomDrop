package ru.mrflaxe.randomdrop;

import org.bukkit.plugin.java.JavaPlugin;

import ru.mrflaxe.randomdrop.listeners.PlayerDeathListener;
import ru.soknight.lib.configuration.Configuration;

public class RandomDrop extends JavaPlugin{
	
	private Configuration config;
	
	@Override
	public void onEnable() {
		initConfigs();
		
		registerEvents();
	}
	
	private void initConfigs() {
		this.config = new Configuration(this, "config.yml");
		config.refresh();
	}
	
	private void registerEvents() {
		new PlayerDeathListener(config).register(this);
	}
}
