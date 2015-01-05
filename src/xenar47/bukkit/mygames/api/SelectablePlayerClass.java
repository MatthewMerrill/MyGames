package xenar47.bukkit.mygames.api;

import org.bukkit.inventory.ItemStack;

public abstract class SelectablePlayerClass extends PlayerClass {

	public abstract ItemStack menuIcon();
	public abstract String shortDescription();
	public abstract String[] weaponNames();

}
