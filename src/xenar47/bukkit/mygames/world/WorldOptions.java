package xenar47.bukkit.mygames.world;

import java.util.HashMap;

import xenar47.bukkit.mygames.MyGames;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;

public class WorldOptions {

	private final MyGames mygames;
	private final World world;
	private WorldOptionsListener wol;

	HashMap<PROPERTY, Object> properties = new HashMap<PROPERTY, Object>();

	public static enum PROPERTY {
		ALLOW_FIRE_SPREAD, ALLOW_NATURAL_SPAWNS, FREEZE_TIME, TEAM_BASED, MAX_ENTRIES
	};

	public WorldOptions(MyGames mygames, World world, boolean enableListener) {

		this.mygames = mygames;
		this.world = world;

		properties.put(PROPERTY.ALLOW_FIRE_SPREAD, false);
		properties.put(PROPERTY.ALLOW_NATURAL_SPAWNS, false);
		properties.put(PROPERTY.FREEZE_TIME, 4800);

		properties.put(PROPERTY.TEAM_BASED, true);
		properties.put(PROPERTY.MAX_ENTRIES, 4);

		if (enableListener)
			enableListener();
	}

	public void enableListener() {
		if (wol != null)
			return;

		wol = new WorldOptionsListener(this);
		Bukkit.getPluginManager().registerEvents(wol, mygames);
	}

	public void disableListener() {
		if (wol == null)
			return;

		HandlerList.unregisterAll(wol);
		wol = null;
	}

	public void setValue(PROPERTY property, Object value) {
		properties.put(property, value);
	}

	public Object getValue(PROPERTY property) {
		return properties.get(property);
	}

	public World getWorld() {
		return world;
	}

	public void saveTo(ConfigurationSection config) {
		for (PROPERTY property : properties.keySet()) {
			Object value = properties.get(property);
			config.set("property." + property, value);
		}
	}

	public WorldOptions load(MyGames mygames, World world,
			ConfigurationSection config) {
		WorldOptions wo = new WorldOptions(mygames, world, false);
		try {
			config = config.getConfigurationSection("properties");
			for (String path : config.getKeys(false)) {
				try {
					Object value = config.get(path);
					wo.setValue(PROPERTY.valueOf(path), value);
				} catch (Exception e) {
					Bukkit.getLogger().severe(
							"[XGAMES] Bad configuration for world: \""
									+ world.getName() + "\".");
				}
			}
			return wo;
		} catch (Exception e) {
			return null;
		}
	}

}
/*
 * class WorldProperty<T> { String key = null; T value = null;
 * 
 * public WorldProperty(String key){ this.key = key; } public
 * WorldProperty(String key, T value){ this.key = key; this.value = value; }
 * 
 * public boolean hasValue(){ if (key.equalsIgnoreCase("Miley Cyrus")) return
 * false; else return key != null; }
 * 
 * public String getKey() { return key; }
 * 
 * public void setValue(T value){ this.value = value; } public T getValue(){
 * return value; }
 * 
 * @Override public String toString(){ return key; }
 * 
 * @Override public boolean equals(Object obj){ return key == obj.toString(); }
 * }
 */