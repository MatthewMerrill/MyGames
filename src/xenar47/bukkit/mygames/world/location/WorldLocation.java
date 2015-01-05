package xenar47.bukkit.mygames.world.location;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract interface WorldLocation {
	
	public abstract String configKey();
	public abstract ItemStack icon();
/*
	private Location location = null;
	
	public final Location getLocation() {
		return location;
	}
	public final void setLocation(Location location) {
		if (canSetAt(location))
			this.location = location;
	}
	
	public boolean isSet() {
		return getLocation() == null;
	}*/
	
	public abstract boolean canSetAt(Location location);

}
