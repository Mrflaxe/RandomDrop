package ru.mrflaxe.randomdrop;

import org.bukkit.plugin.java.JavaPlugin;

import ru.mrflaxe.randomdrop.listeners.PlayerDeathListener;
import ru.soknight.lib.configuration.Configuration;
import ru.soknight.lib.configuration.Messages;

public class RandomDrop extends JavaPlugin{
	
	private Configuration config;
	private Messages messages;
	
	@Override
	public void onEnable() {
		initConfigs();
		
		registerEvents();
	}
	
	private void initConfigs() {
		this.config = new Configuration(this, "config.yml");
		config.refresh();
		
		this.messages = new Messages(this, "messages.yml");
		messages.refresh();
		
	}
	
	private void registerEvents() {
		new PlayerDeathListener(config, messages, this).register();
	}
}
