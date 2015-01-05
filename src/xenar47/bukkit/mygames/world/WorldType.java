package xenar47.bukkit.mygames.world;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import xenar47.bukkit.mygames.world.location.SpawnLocation;
import xenar47.bukkit.mygames.world.location.WorldLocation;

public interface WorldType {
	
	public abstract String name();
	
	public abstract ArrayList<SpawnLocation> getSpawnLocations();
	public abstract ArrayList<WorldLocation> getCustomLocations();
	public abstract ItemStack icon();

}
