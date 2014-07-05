package xenar47.bukkit.mygames;

import org.bukkit.configuration.file.FileConfiguration;

public class GameConfigManager {
	
	public static final String GAME_PREFIX = "game.";
	public static final String GAME_SUFFIX_ENABLED = ".enabled";
	
	private MyGames mygames;
	private FileConfiguration defConfig;
	
	public GameConfigManager(MyGames mygames) {
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
	
	public boolean getGameEnabled(String gameName) {
		
		String path = getEnabledPath(gameName);
		
		if (!defConfig.contains(path)) {
			defConfig.set(path, true);
			return true;
		} else {
			return defConfig.getBoolean(path, true);
		}
	}
	
	public String getEnabledPath(String gameName) {
		return GAME_PREFIX + gameName + GAME_SUFFIX_ENABLED;
	}
	
}
