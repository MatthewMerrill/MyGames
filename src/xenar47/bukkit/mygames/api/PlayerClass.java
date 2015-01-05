package xenar47.bukkit.mygames.api;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public abstract class PlayerClass {
	
	public abstract String getName();
	
	/**
	 * Prepare a player to play as a certain class.
	 * Although setting armor is possible, setting a helmet is not advised
	 * because of its usefulness as a game mechanic.
	 * @param player
	 */
	public abstract void setup(Player player);
	public abstract HashMap<PotionEffectType, Integer> getLastingEffects();
	
	private static HashMap<UUID, String> classes = new HashMap<UUID, String>();
	private static HashMap<String, PlayerClass> ids = new HashMap<String, PlayerClass>();
	
	/**
	 * Sets class but does not setup player. If desired, call {@link PlayerClass#setup(Player)});
	 * @param player
	 * @param pc
	 */
	public static void setClass(Player player, PlayerClass pc) {
		classes.put(player.getUniqueId(), pc.getIdentifier());
	}
	
	public static PlayerClass getClass(Player player) {
		String key = classes.get(player.getUniqueId());
		if (key == null)
			return null;
		
		return ids.get(key);
	}
	
	public static void setupPlayer(Player player) {
		PlayerClass pc = getClass(player);
		if (pc != null)
			pc.setup(player);
	}
	
	public static HashMap<PotionEffectType, Integer> getLastingEffects(Player player) {
		PlayerClass pc = getClass(player);
		if ((pc == null) || (pc.getLastingEffects() == null))
			return new HashMap<PotionEffectType, Integer>();
		
		return pc.getLastingEffects();
	}
	
	private String identifier = null;
	
	private void getNewId() {
		if (!ids.keySet().contains(getName())) {
			identifier = getName();
		} else {
			int id = 0;
			while (ids.containsKey(getName() + id))
				id++;
			
			identifier = getName() + id;
		}
		ids.put(identifier, this);
	}
	
	private String getIdentifier() {
		if (identifier == null)
			getNewId();
		return identifier;
	}
	
}
