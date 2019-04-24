package com.alchemi.twerkit;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class ReloadCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission(command.getPermission())) {
			FileConfiguration conf = main.instance.getConfig();
			main.instance.range = conf.getInt("range");
			main.instance.growthChance = conf.getInt("growthChance");
			main.instance.crops = conf.getStringList("crops");
			main.instance.cropMaterials.clear();
			for (String crop : main.instance.crops)
				try { main.instance.cropMaterials.add(Material.getMaterial(crop));
				} catch (Exception localException) {}
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&aTwerkIT&7] &aConfig reloaded."));
		}
		return true;
	}

}
