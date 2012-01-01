package com.thezorro266.simpleregionmarket;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class LimitHandler {
	private static YamlConfiguration limitConfig;

	private static int limitregions = -1;
	private static Map<String, Integer> limitregionworlds = new HashMap<String, Integer>(); // Limits regions per world - Low Priority
	//private static Map<String, Integer> limitregiongroups = new HashMap<String, Integer>(); // Limits regions per groups - Mid Priority
	private static Map<String, Integer> limitregionplayers = new HashMap<String, Integer>(); // Limits regions per player - High Priority

	private static int limitrooms = -1;
	private static Map<String, Integer> limitroomworlds = new HashMap<String, Integer>(); // Limits rooms per world - Low Priority
	//private static Map<String, Integer> limitroomgroups = new HashMap<String, Integer>(); // Limits rooms per groups - Mid Priority
	private static Map<String, Integer> limitroomplayers = new HashMap<String, Integer>(); // Limits rooms per player - High Priority

	// TODO General permission groups / Which permissions plugins?
	// TODO Parent region limits

	public static void saveLimits() {
		limitConfig = new YamlConfiguration();

		limitConfig.createSection("regions");
		limitConfig.set("regions.global", limitregions);

		limitConfig.createSection("regions.worlds");
		for(String key: limitregionworlds.keySet()) {
			limitConfig.set("regions.worlds." + key, limitregionworlds.get(key));
		}

		limitConfig.createSection("regions.players");
		for(String key: limitregionplayers.keySet()) {
			limitConfig.set("regions.players." + key, limitregionplayers.get(key));
		}

		limitConfig.createSection("rooms");
		limitConfig.set("rooms.global", limitrooms);

		limitConfig.createSection("rooms.worlds");
		for(String key: limitroomworlds.keySet()) {
			limitConfig.set("rooms.worlds." + key, limitroomworlds.get(key));
		}

		limitConfig.createSection("rooms.players");
		for(String key: limitroomplayers.keySet()) {
			limitConfig.set("rooms.players." + key, limitroomplayers.get(key));
		}

		try {
			limitConfig.save(SimpleRegionMarket.plugin_dir + "limits.yml");
		} catch (IOException e) {
			LanguageHandler.outputConsole(Level.SEVERE, "Could not save limits.");
		}
	}

	public static void loadLimits() {
		limitConfig = YamlConfiguration.loadConfiguration(new File(SimpleRegionMarket.plugin_dir + "limits.yml"));

		ConfigurationSection path;
		for(String main: limitConfig.getKeys(false)) {
			path = limitConfig.getConfigurationSection(main);
			for(String limiter: path.getKeys(false)) {
				if(limiter.equalsIgnoreCase("global")) {
					if(main.equalsIgnoreCase("regions")) {
						limitregions = path.getInt(limiter, -1);
					} else if(main.equalsIgnoreCase("rooms")) {
						limitrooms = path.getInt(limiter, -1);
					}
					continue;
				}
				path = limitConfig.getConfigurationSection(main).getConfigurationSection(limiter);
				for(String key: path.getKeys(false)) {
					if(main.equalsIgnoreCase("regions")) {
						if(limiter.equalsIgnoreCase("worlds")) {
							limitregionworlds.put(key, path.getInt(key));
						} else if(limiter.equalsIgnoreCase("players")) {
							limitregionplayers.put(key, path.getInt(key));
						}
					} else if(main.equalsIgnoreCase("rooms")) {
						if(limiter.equalsIgnoreCase("worlds")) {
							limitroomworlds.put(key, path.getInt(key));
						} else if(limiter.equalsIgnoreCase("players")) {
							limitroomplayers.put(key, path.getInt(key));
						}
					}
				}
			}
		}
	}

	private static int countPlayerOwnRegion(Player p) {
		// TODO Only regions which were buyed over this plugin ?
		if(p != null) {
			WorldGuardPlugin tmp = SimpleRegionMarket.getWorldGuard();
			return tmp.getGlobalRegionManager().get(p.getWorld()).getRegionCountOfPlayer(tmp.wrapPlayer(p));
		}
		return 0;
	}

	private static int countPlayerRentRoom(Player p) {
		int count = 0;
		for(int i = 0; i < SimpleRegionMarket.getAgentManager().getAgentList().size(); i++) {
			SignAgent now = SimpleRegionMarket.getAgentManager().getAgentList().get(i);
			if(now != null
					&& now.getMode() == SignAgent.MODE_RENT_HOTEL
					&& now.getRent() == p.getName()) {
				count++;
			}
		}
		return count;
	}

	public static void setGlobalBuyLimit(int limit) {
		if(limit < -1) {
			limit = -1;
		}

		limitregions = limit;
	}

	public static int getGlobalBuyLimit() {
		return limitregions;
	}

	public static void setBuyWorldLimit(World w, int limit) {
		if(w != null) {
			if(limit < -1) {
				limit = -1;
			}

			limitregionworlds.put(w.getName(), limit);
		}
	}

	public static int getBuyWorldLimit(World w) {
		if(limitregionworlds.get(w.getName()) != null)
			return limitregionworlds.get(w.getName());
		else
			return -1;
	}

	public static void setBuyPlayerLimit(Player p, int limit) {
		if(p != null) {
			if(limit < -1) {
				limit = -1;
			}

			limitregionplayers.put(p.getName(), limit);
		}
	}

	public static int getBuyPlayerLimit(Player p) {
		if(limitregionplayers.get(p.getName()) != null)
			return limitregionplayers.get(p.getName());
		else
			return -1;
	}

	public static void setGlobalRentLimit(int limit) {
		if(limit < -1) {
			limit = -1;
		}

		limitrooms = limit;
	}

	public static int getGlobalRentLimit() {
		return limitrooms;
	}

	public static void setRentWorldLimit(World w, int limit) {
		if(w != null) {
			if(limit < -1) {
				limit = -1;
			}

			limitroomworlds.put(w.getName(), limit);
		}
	}

	public static int getRentWorldLimit(World w) {
		if(limitroomworlds.get(w.getName()) != null)
			return limitroomworlds.get(w.getName());
		else
			return -1;
	}

	public static void setRentPlayerLimit(Player p, int limit) {
		if(p != null) {
			if(limit < -1) {
				limit = -1;
			}

			limitroomplayers.put(p.getName(), limit);
		}
	}

	public static int getRentPlayerLimit(Player p) {
		if(limitroomplayers.get(p.getName()) != null)
			return limitroomplayers.get(p.getName());
		else
			return -1;
	}

	public static boolean limitCanBuy(Player p) {
		if(p != null) {
			String playername = p.getName();
			if(limitregionplayers.containsKey(playername))
				return (countPlayerOwnRegion(p) < limitregionplayers.get(playername));
			else if(p.getWorld() != null
					&& limitregionworlds.containsKey(p.getWorld().getName())
					&& limitregionworlds.get(p.getWorld().getName()) != -1)
				return (countPlayerOwnRegion(p) < limitregionworlds.get(p.getWorld().getName()));
			else if(limitregions != -1)
				return (countPlayerOwnRegion(p) < limitregions);
			return true;
		}
		return false;
	}

	public static boolean limitCanRent(Player p) {
		if(p != null) {
			String playername = p.getName();
			if(limitroomplayers.containsKey(playername))
				return (countPlayerRentRoom(p) < limitroomplayers.get(playername));
			else if(p.getWorld() != null
					&& limitroomworlds.containsKey(p.getWorld().getName())
					&& limitroomworlds.get(p.getWorld().getName()) != -1)
				return (countPlayerRentRoom(p) < limitroomworlds.get(p.getWorld().getName()));
			else if(limitrooms != -1)
				return (countPlayerRentRoom(p) < limitrooms);
			return true;
		}
		return false;
	}
}