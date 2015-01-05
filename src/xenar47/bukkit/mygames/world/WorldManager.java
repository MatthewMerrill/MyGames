package xenar47.bukkit.mygames.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.util.com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.Utils;
import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.world.location.SpawnLocation;
import xenar47.bukkit.mygames.world.location.WorldLocation;

public class WorldManager {

	private MyGames mygames;
	private WorldConfigManager wcm;
	
	//private HashMap<WorldType, ArrayList<String>> worldsByType = new HashMap<WorldType, ArrayList<String>>();
	//private ArrayList<String> worlds = new ArrayList<String>();
	private HashMap<String, World> dupes = new HashMap<String, World>();
	
	public WorldManager(MyGames mygames) {
		this.mygames = mygames;
		this.wcm = new WorldConfigManager(mygames);
		
		/*for (WorldType type : wcm.getWorldTypes()) {
			for (String key : wcm.list) {
				if (wcm.worldCompatible(key, type)) {
					if (!worldsByType.containsKey(type))
						worldsByType.put(type, new ArrayList<String>());
		
					worldsByType.get(type).add(key);
				}
			}
		}*/
	}
	
	public void onDisable() {
		for (String string : dupes.keySet()) {
			destroyCopy(string);
		}
	}
	
	public void addToList(String string) {
		wcm.addToList(string);
	}
	public void removeFromList(String string) {
		wcm.removeFromList(string);
	}
	
	public String getRandomKey(Game game) {
		return Utils.getRandomItem(getCompatibleWorlds(game));
	}
	
	public World getRandomWorldCopy(Game game) {
		String key = getRandomKey(game);
		return getWorldCopy(key);
	}
	
	public boolean keyIsCompatible(String key, Game game) {
		if (game.getLocationTypes() != null) {
			for (WorldLocation loc : game.getLocationTypes()) {
				if (!wcm.hasLocation(key, loc.configKey()))
					return false;
			}
		}
		
		if (game.getLocationTypes() != null) {
			for (DyeColor color : game.getSpawnColors()) {
				SpawnLocation loc = new SpawnLocation(color);
				if (!wcm.hasLocation(key, loc.configKey()))
					return false;
			}
		} else {
			if (wcm.getConfig(key).getConfigurationSection("location.spawn").getKeys(false).isEmpty())
				return false;
		}
		
		return true;
	}

	public ArrayList<String> getCompatibleWorlds(Game game) {
		ArrayList<String> compatible = new ArrayList<String>();
		
		for (String key : wcm.list) {
			if (keyIsCompatible(key, game))
				compatible.add(key);
		}
		
		return compatible;
	}
	
	public World getWorldCopy(String key) {
		if (!wcm.list.contains(key))
			return null;
		
		String mapname = getAvailableName(key);
		
		copyWorld(key, mapname);
		
		World world = Bukkit.getServer().createWorld(new WorldCreator(mapname));
		world = wcm.setWorldOptions(world);
		dupes.put(mapname, world);
		
		return world;
	}
	
	/**
	 * HOLD ON TIGER! CALLING THIS METHOD COULD DELETE YOUR MAPS!
	 */
	public void destroyCopy(String mapname) {
		if (!dupes.containsKey(mapname)) {
			mygames.getLogger().warning("Almost deleted map " + mapname + "! Did someone call WorldManager.destroyCopy(String)?");
			return;
		}
		
		World world = dupes.get(mapname);
		File directory = world.getWorldFolder();
		
		clearWorld(world);
		if (unloadWorld(mapname)) {
			deleteWorld(directory);
		}
	}
	
	private void copyWorld(String key, String mapname) {
		File source = new File(Bukkit.getWorldContainer().getPath() + File.separator + key);
		File target = new File(Bukkit.getWorldContainer().getPath() + File.separator + mapname);
		
		copyWorld(source, target);
	}
	
	private void copyWorld(File source, File target){
		//mygames.getLogger().info("copying from " + source.getPath() + " to " + target.getPath());
		try {
			ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
			if(!ignore.contains(source.getName())) {
				if(source.isDirectory()) {
					if(!target.exists())
						target.mkdirs();
					String files[] = source.list();
					for (String file : files) {
						File srcFile = new File(source, file);
						File destFile = new File(target, file);
						copyWorld(srcFile, destFile);
					}
				} else {
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0)
						out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {
		 
		}
	}
	
	private void clearWorld(World world) {
		for (Entity entity : world.getEntities()) {
			if (entity instanceof Player) {
				((Player)entity).teleport(mygames.lobbyLocation());
			}
		}
	}
	
	private boolean unloadWorld(String mapname) {
		if (Bukkit.getServer().unloadWorld(mapname, false)) {
			mygames.getLogger().info("Successfully unloaded " + mapname);
			dupes.remove(mapname);
			return true;
		} else {
			mygames.getLogger().severe("COULD NOT UNLOAD " + mapname);
			return false;
		}
	}
	
	private boolean deleteWorld(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			for (File file : files) {
				if (file.isDirectory())
					deleteWorld(file);
				else
					file.delete();
			}
		}
		return path.delete();
	}
	
	private boolean isKey(String mapname) {
		try {
			int endIndex = mapname.lastIndexOf('_');
			String suffix = mapname.substring(endIndex);
			Integer.valueOf(suffix);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	private String getKey(String mapname) {
		int endIndex = mapname.lastIndexOf('_');
		String key = mapname.substring(0, endIndex);
		return key;
	}
	
	private String getAvailableName(String key){
		int id = 1;
		
		while (dupes.containsKey(key + "_" + id))
			id++;
		
		return key + "_" + id;
	}
	
	public static Location listToLoc(List<Double> list, World world) {
		if (list == null)
			return null;
		return new Location(world, list.get(0), list.get(1), list.get(2));
	}

	public Location getRandomSpawn(World world) {
		String key = getKey(world.getName());
		FileConfiguration config = wcm.getConfig(key);
		config.getConfigurationSection("location.spawn");
		
		ArrayList<String> keys = Lists.newArrayList(config.getKeys(false));
		
		Location loc = null;
		
		Random r = new Random();
		while (keys.size() > 0) {
			int index = r.nextInt(keys.size());
			loc = getLocation(world, keys.get(index));
			if (loc != null)
				return loc;
			else
				keys.remove(index);
		}
		
		mygames.getLogger().log(Level.SEVERE, "There are no spawns in world " + key);
		return null;
	}

	public Location getLocation(World world, String locationKey) {
		return listToLoc(wcm.getLocation(getKey(world.getName()), locationKey), world);
	}
}
