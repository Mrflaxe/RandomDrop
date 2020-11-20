package ru.mrflaxe.randomdrop;

import org.bukkit.plugin.java.JavaPlugin;

import ru.mrflaxe.randomdrop.listeners.PlayerActionListener;
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
		new PlayerActionListener(config, messages, this).register();
	}
}
