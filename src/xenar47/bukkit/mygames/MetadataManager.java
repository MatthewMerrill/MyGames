package xenar47.bukkit.mygames;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class MetadataManager {

	private final MyGames plugin;
	public static final String KEY = "mygames";
	public static final String MODE = "mygames.mode";
	public static final String TEAM = "mygames.team";
	public static final String CLASS = "mygames.class";
	
	public static final String GAME = "game";
	
	public static final int OTHER_GAME = -1;
	public static final int INGAME = 0;
	public static final int LOBBY = 1;
	public static final int SETUP = 2;

	public MetadataManager(MyGames plugin) {
		this.plugin = plugin;
	}

	public boolean setInGame(Player player, String gameName) {
		if (inOtherGame(player))
			return false;

		player.setMetadata(MODE, new FixedMetadataValue(plugin, INGAME));
		player.setMetadata(GAME, new FixedMetadataValue(plugin, gameName));
		
		return true;
	}

	public boolean setInSetup(Player player) {
		if (inOtherGame(player))
			return false;

		player.setMetadata(MODE, new FixedMetadataValue(plugin, SETUP));
		player.setMetadata(GAME, new FixedMetadataValue(plugin, null));
		
		return true;
	}

	public boolean setInLobby(Player player) {
		if (inOtherGame(player))
			return false;

		player.setMetadata(MODE, new FixedMetadataValue(plugin, LOBBY));
		player.setMetadata(GAME, new FixedMetadataValue(plugin, null));
		
		return true;
	}
	
	public void setInOther(Player player) {
		int mode = getMode(player);
		if (mode == OTHER_GAME)
			return;
		
		player.setMetadata(MODE, new FixedMetadataValue(plugin, OTHER_GAME));
	}
	

	public int getMode(Player player) {
		try {
			
			if (inOtherGame(player))
				return OTHER_GAME;
			
			int mode = player.getMetadata(MODE).get(0).asInt();
			return mode;
		} catch (Exception e) {
			return LOBBY;
		}
	}
	
	public String getGame(Player player) {
		List<MetadataValue> meta = player.getMetadata(GAME);
		if (meta == null || meta.size() < 1)
			return plugin.lm.getCurrentGame(player.getUniqueId());
		
		return player.getMetadata(GAME).get(0).asString();
	}
	
	public boolean inOtherGame(Player player) {
		try {
			String game = getGame(player);
			if ((!game.trim().equalsIgnoreCase("")) && plugin.getGame(game) == null) {
				setInOther(player);
				return true;
			}
		} catch (Exception e) {
			//Not worried. Will catch if game == null.
		}
		return false;
	}

	public void remove(Player player) {
		if (!inOtherGame(player)) {
			player.setMetadata(GAME, null);
		}
		player.setMetadata(MODE, null);
	}

}
