package xenar47.bukkit.mygames.world;

import java.util.HashMap;

import org.bukkit.World;

public class WorldManager {

	private HashMap<String, World> worlds = new HashMap<String, World>();

	public World getWorld(String string) {
		return worlds.get(string);
	}

}
