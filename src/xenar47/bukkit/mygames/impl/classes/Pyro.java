package xenar47.bukkit.mygames.impl.classes;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import xenar47.bukkit.mygames.api.SelectablePlayerClass;

public class Pyro extends SelectablePlayerClass {
	
	private static ItemStack[] getWeapons() {
		ItemStack[] weaps = new ItemStack[2];
		
		weaps[0] = new ItemStack(Material.FLINT_AND_STEEL);
		weaps[1] = new ItemStack(Material.IRON_AXE);
		
		return weaps;
	}

	@Override
	public String getName() {
		return "Pyro";
	}

	@Override
	public void setup(Player player) {
		player.getInventory().addItem(getWeapons());
	}

	@Override
	public HashMap<PotionEffectType, Integer> getLastingEffects() {
		HashMap<PotionEffectType, Integer> effects = new HashMap<PotionEffectType, Integer>();
		effects.put(PotionEffectType.FIRE_RESISTANCE, 3);
		return effects;
	}

	@Override
	public ItemStack menuIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String shortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] weaponNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
