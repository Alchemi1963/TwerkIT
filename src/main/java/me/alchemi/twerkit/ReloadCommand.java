package me.alchemi.twerkit;

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
			FileConfiguration conf = main.getInstance().getConfig();
			main.getInstance().range = conf.getInt("range");
			main.getInstance().growthChance = conf.getInt("growthChance");
			main.getInstance().crops = conf.getStringList("crops");
			main.getInstance().cropMaterials.clear();
			for (String crop : main.getInstance().crops)
				try { main.getInstance().cropMaterials.add(Material.getMaterial(crop));
				} catch (Exception localException) {}
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&aTwerkIT&7] &aConfig reloaded."));
		}
		return true;
	}

}
