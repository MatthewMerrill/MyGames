package xenar47.bukkit.mygames.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import xenar47.bukkit.mygames.MyGames;

public abstract class Weapon {
	
	private static HashMap<String, Weapon> registeredWeapons = new HashMap<String, Weapon>();
	
	protected Weapon() {
		
	}
	
	/**
	 * Get preferred name for weapon. If this name is taken, it will be appended by the first available integer.
	 * @return
	 */
	public abstract String getName();
	public abstract void registerNecessaryListeners();
	
	public abstract boolean primary(Game game, Player player);
	public abstract boolean secondary(Game game, Player player);

	/**
	 * Called when a player uses this weapon in a melee attack on someone else.
	 * 
	 * @param game
	 * @param player
	 * @param victim
	 * @return How much damage should be done to the victim
	 */
	public abstract int melee(Game game, Player player, Player victim);
	public abstract boolean interact(Game game, Player player, Entity target);
	public abstract void reload(Game game, Player player);

	public abstract ItemStack getBaseItem();
	
	public final boolean isRegistered() {
		return isRegistered(getName());
	}
	
	public void generateExplosion(Location loc, float power, Player player) {
		TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
		tnt.setFuseTicks(1);
		tnt.setYield(power);
		tnt.setIsIncendiary(false);
		tnt.setMetadata("Owner", new FixedMetadataValue(MyGames.getInstance(), player.getName()));
	}
	
	public static final boolean isRegistered(String name) {
		return registeredWeapons.containsKey(name); 
	}
	
	public static boolean register(Weapon weapon) {
		
		if (weapon.isRegistered())
			return true;
		
		try{
			registeredWeapons.put(weapon.getName(), weapon);
			weapon.registerNecessaryListeners();
			return true;
		} catch (Exception e) {
			Bukkit.getLogger().severe("Error while registering Weapon: " + weapon.getName());
			return false;
		}
	}
	
	public static ItemStack createWeapon(Class<? extends Weapon> w) {
		
		Weapon weapon;
		
		try {
			weapon = w.newInstance();
			
			if (!weapon.isRegistered()) {
				if (!register(weapon))
					return null;
			}
			
			ItemStack is = weapon.getBaseItem();
			
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.RED + weapon.getName());
			
			List<String> lores = new ArrayList<String>();
			lores.add("Weapon");
			lores.add(weapon.getName());
			
			im.setLore(lores);
			
			is.setItemMeta(im);
		
			return is;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public static Weapon parseWeapon(ItemStack itemStack) {
		
		if (itemStack == null)
			return null;
		
		if (isWeapon(itemStack)) {
			return registeredWeapons.get(getWeaponName(itemStack));
		}
		
		return null;
	}
	
	public static String getWeaponName(ItemStack itemStack) {
		
		try {
			List<String> lore = itemStack.getItemMeta().getLore();
			
			if (lore.get(0).equalsIgnoreCase("Weapon")) {
				return lore.get(1);
			}
		} catch(Exception e) {
			return null;
		}
		
		return null;
	}
	
	public static boolean isWeapon(ItemStack itemStack) {
		
		if (itemStack == null)
			return false;
		
		String name = getWeaponName(itemStack);
		
		return (name != null) && (isRegistered(name));
	}
	
}