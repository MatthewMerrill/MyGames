package xenar47.bukkit.mygames.world.location;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import xenar47.bukkit.mygames.world.WorldType;

public class WorldBasic implements WorldType {

	ArrayList<SpawnLocation> spawns;
	
	public WorldBasic() {
		spawns = new ArrayList<SpawnLocation>();
		
		SpawnLocation spawnRed = new SpawnLocation(DyeColor.RED);
		SpawnLocation spawnBlue = new SpawnLocation(DyeColor.BLUE);
		SpawnLocation spawnGreen = new SpawnLocation(DyeColor.GREEN);
		SpawnLocation spawnYellow = new SpawnLocation(DyeColor.YELLOW);
		
		spawns.add(spawnRed);
		spawns.add(spawnBlue);
		spawns.add(spawnGreen);
		spawns.add(spawnYellow);
		
	}
	
	@Override
	public String name() {
		return "basic";
	}

	@Override
	public ArrayList<SpawnLocation> getSpawnLocations() {
		return spawns;
	}
	
	@Override
	public ArrayList<WorldLocation> getCustomLocations() {
		return null;
	}
	
	@Override
	public ItemStack icon() {
		Wool wool = new Wool();
		wool.setColor(DyeColor.WHITE);
		return wool.toItemStack();
	}

}
