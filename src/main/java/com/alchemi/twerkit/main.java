package com.alchemi.twerkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener {
	public FileConfiguration conf;
	public List<Material> cropMaterials = new ArrayList<Material>();
	public List<String> crops = new ArrayList<String>();
	public int range;
	public int growthChance;
	public Random rand;
	
	public static main instance;
	
	public void onEnable() {
		instance = this;
		
		cropMaterials.add(Material.WHEAT);
		cropMaterials.add(Material.POTATOES);
		cropMaterials.add(Material.CARROTS);
		cropMaterials.add(Material.PUMPKIN_STEM);
		cropMaterials.add(Material.MELON_STEM);
		cropMaterials.add(Material.BEETROOTS);
		cropMaterials.add(Material.CACTUS);
		cropMaterials.add(Material.SUGAR_CANE);
		cropMaterials.add(Material.ACACIA_SAPLING);
		cropMaterials.add(Material.BIRCH_SAPLING);
		cropMaterials.add(Material.DARK_OAK_SAPLING);
		cropMaterials.add(Material.JUNGLE_SAPLING);
		cropMaterials.add(Material.OAK_SAPLING);
		cropMaterials.add(Material.SPRUCE_SAPLING);
		
		for (Material mat : cropMaterials) {
			crops.add(mat.name());
		}
		conf = getConfig();
		conf.addDefault("range", Integer.valueOf(5));
		conf.addDefault("growthChance", Integer.valueOf(20));
		conf.addDefault("crops", crops);
		
		if (!conf.isSet("range")) conf.set("range", Integer.valueOf(5));
		if (!conf.isSet("growthChance")) conf.set("growthChance", Integer.valueOf(20));
		if (!conf.isSet("crops")) conf.set("crops", crops);
		saveConfig();
		
		range = conf.getInt("range");
		growthChance = conf.getInt("growthChance");
		crops = conf.getStringList("crops");
		cropMaterials.clear();
		for (String crop : crops)
			try { cropMaterials.add(Material.getMaterial(crop));
			} catch (Exception localException) {}
		
		rand = new Random();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginCommand("twerkit").setExecutor(new ReloadCommand());
	}
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent e) {
		if (!e.getPlayer().hasPermission("twerkit.twerk")) return;
		Location loc = e.getPlayer().getLocation();
		World world = loc.getWorld();
		
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++)
					if (cropMaterials.contains(world.getBlockAt(loc.clone().add(x, y, z)).getType())) {
						
						Block b = world.getBlockAt(loc.clone().add(x, y, z));
						BlockData bd = b.getBlockData();
						
						if (bd instanceof Ageable) {
							int cAge = ((Ageable)bd).getAge();
							
							if ((rand.nextInt(1000) <= growthChance / 2) && ((bd.getMaterial().equals(Material.CACTUS)) || 
									(bd.getMaterial().equals(Material.SUGAR_CANE))) && (e.getPlayer().hasPermission("twerkit.cacsug"))) {
									int count = 0;
									for (int y1 = -3; y1 <= 0; y1++)
									{
										if (world.getBlockAt(b.getLocation().clone().add(0.0D, y1, 0.0D)).getType().equals(b.getType())) { count++;
										}
									}
									
									if ((count < 3) && (world.getBlockAt(b.getLocation().clone().add(0.0D, 1.0D, 0.0D)).getType().equals(Material.AIR)))
									{
										final Block nB = world.getBlockAt(b.getLocation().clone().add(0.0D, 1.0D, 0.0D));
										nB.setType(b.getType());
										world.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(x, y, z), 8, 0.5D, 0.5D, 0.5D);
										
										if ((bd.getMaterial().equals(Material.CACTUS)) && (isBlockAdjecent(nB.getLocation())))
										{
											Bukkit.getScheduler().runTaskLater(this, new Runnable()
											{

												public void run()
												{
													nB.breakNaturally();
												}
												
											}, 10L);
										}	
									}	
								}
							
							else if ((cAge < ((Ageable)bd).getMaximumAge()) && (rand.nextInt(100) <= growthChance)) {
								((Ageable)bd).setAge(cAge + 1);
								world.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(x, y, z), 8, 0.5D, 0.5D, 0.5D);
							}
						} 
						
						if (bd instanceof Sapling) {
							int stage = ((Sapling) bd).getStage();
						
							if ((stage < ((Sapling) bd).getMaximumStage()) && (rand.nextInt(100) <= growthChance)) {
								((Sapling)bd).setStage(stage + 1);
								world.spawnParticle(Particle.VILLAGER_HAPPY, b.getLocation(), 8, 0.5D, 0.5D, 0.5D);
								
							} else if ((stage == ((Sapling) bd).getMaximumStage()) && 
								((float) rand.nextInt(1000) <= (float) growthChance ) && 
								(e.getPlayer().hasPermission("twerkit.trees"))) {
								
								TreeType type = null;
								
								if (b.getType().equals(Material.ACACIA_SAPLING)) {
									type = TreeType.ACACIA;
								}
								else if (b.getType().equals(Material.BIRCH_SAPLING)) {
									type = TreeType.BIRCH;
								}
								else if (b.getType().equals(Material.OAK_SAPLING)) {
									type = TreeType.TREE;
								}
								else if (b.getType().equals(Material.SPRUCE_SAPLING)) {
									type = TreeType.REDWOOD;
								}
								
								if (type == null) return;
								
								else if (b.getWorld().generateTree(b.getLocation(), type)) {
									switch (type) {
									case ACACIA: 
										b.setType(Material.ACACIA_LOG);
										break;
									case BIRCH: 
										b.setType(Material.BIRCH_LOG);
										break;
									case DARK_OAK: 
										b.setType(Material.DARK_OAK_LOG);
										break;
									case SMALL_JUNGLE: 
										b.setType(Material.JUNGLE_LOG);
										break;
									case JUNGLE: 
										b.setType(Material.JUNGLE_LOG);
										break;
									case TREE: 
										b.setType(Material.OAK_LOG);
										break;
									case REDWOOD: 
										b.setType(Material.SPRUCE_LOG);
										break;
									case TALL_REDWOOD: 
										b.setType(Material.SPRUCE_LOG);
										break;
									default:
										b.setType(Material.OAK_LOG);
										break;
									}
									return;
								}
							}
						}
						

						b.setBlockData(bd);
					}
			}
		}
	}
	
	private boolean isBlockAdjecent(Location location) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (Math.abs(x) != Math.abs(z))
				{
					if (location.getWorld().getBlockAt(location.clone().add(x, 0.0D, z)).getType() != Material.AIR) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
