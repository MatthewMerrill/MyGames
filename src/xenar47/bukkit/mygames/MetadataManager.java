package xenar47.bukkit.mygames;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class MetadataManager {

	public Plugin plugin;
	public final String KEY = "mygames";
	public final String MODE = "mygames.mode";
	public final int GAME = 0;
	public final int LOBBY = 1;
	public final int SETUP = -1;

	public MetadataManager(MyGames plugin) {
		this.plugin = plugin;
	}

	public void setInGame(Player player) {
		player.setMetadata(MODE, new FixedMetadataValue(plugin, GAME));
	}

	public void setInLobby(Player player) {
		player.setMetadata(MODE, new FixedMetadataValue(plugin, LOBBY));
	}

	public void setInSetup(Player player) {
		player.setMetadata(MODE, new FixedMetadataValue(plugin, SETUP));
	}

	public int getMode(Player player) {
		try {
			int mode = player.getMetadata(MODE).get(0).asInt();
			return mode;
		} catch (Exception e) {
			return LOBBY;
		}
	}

	public void remove(Player player) {
		player.setMetadata(MODE, null);
	}

}
