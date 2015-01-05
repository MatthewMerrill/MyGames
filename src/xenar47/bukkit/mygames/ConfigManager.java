package xenar47.bukkit.mygames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	
	public static final String GAME_PREFIX = "game.";
	public static final String SUFFIX_ENABLED = ".enabled";
	
	public static final String LISTENER_LOBBY = "listener.lobby.enabled";
	public static final String LISTENER_SIGN = "listener.sign.enabled";
	
	public static final String LOCATION_SIGN = "signs.locations";
	
	public static final String SPAWN_WORLD = "customSpawn.world.name";
	public static final String SPAWN_WORLD_ENABLED = "customSpawn.world.enabled";
	public static final String SPAWN_LOCATION = "customSpawn.location.loc";
	public static final String SPAWN_LOCATION_ENABLED = "customSpawn.location.enabled";
	
	private MyGames mygames;
	private FileConfiguration defConfig;
	
	public ConfigManager(MyGames mygames) {
		this.mygames = mygames;
		loadConfig();
	}
	
	public void loadConfig() {
		mygames.saveDefaultConfig();
		defConfig = mygames.getConfig();		
	}
	
	public void saveConfig() {
		mygames.saveConfig();
	}
	
	public boolean isLobbyListenerEnabled() {
		String path = LISTENER_LOBBY;
		
		if (!defConfig.contains(path)) {
			defConfig.set(path, false);
			return false;
		} else {
			return defConfig.getBoolean(path, false);
		}
	}
	
	public boolean isSignListenerEnabled() {
		String path = LISTENER_SIGN;
		
		if (!defConfig.contains(path)) {
			defConfig.set(path, true);
			return true;
		} else {
			return defConfig.getBoolean(path, true);
		}
	}
	
	
	public boolean isGameEnabled(String gameName) {
		
		String path = getGameEnabledPath(gameName);
		
		if (!defConfig.contains(path)) {
			defConfig.set(path, true);
			return true;
		} else {
			return defConfig.getBoolean(path, true);
		}
	}
	
	public String getGameEnabledPath(String gameName) {
		return GAME_PREFIX + gameName + SUFFIX_ENABLED;
	}
	
	public Location getSpawnLocation() {
		
		if (!(defConfig.getBoolean(SPAWN_WORLD_ENABLED, false)))
			return Bukkit.getWorlds().get(0).getSpawnLocation();
		
		String w = defConfig.getString(SPAWN_WORLD);
		World world = Bukkit.getWorld(w);

		if (!(defConfig.getBoolean(SPAWN_LOCATION_ENABLED, false)))
			return world.getSpawnLocation();
		
		String l = defConfig.getString(SPAWN_LOCATION);
		String[] s = l.split(";");
		
		return new Location(world, Double.parseDouble(s[0]),Double.parseDouble(s[1]),Double.parseDouble(s[2]),
				Float.parseFloat(s[3]), Float.parseFloat(s[4]));
	}
	
}
