package xenar47.bukkit.mygames;

import java.util.List;

import xenar47.bukkit.mygames.world.WorldConfigManager;
import xenar47.bukkit.mygames.world.WorldConfigManager.LOCATIONS;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;

public class TeamWorld {

	public static Location getLocation(WorldConfigManager wcm, World world,
			LOCATIONS l) {

		String key = getLocationPath(l);

		FileConfiguration config = wcm.getConfig(world.getName());
		List<Double> d = config.getDoubleList(key);
		if (d != null && d.size() == 3)
			return new Location(world, d.get(0), d.get(1), d.get(2));
		else
			return null;
	}

	public static void setLocation(WorldConfigManager wcm, World world,
			LOCATIONS l, Location loc) {

		String key = getLocationPath(l);

		FileConfiguration config = wcm.getConfig(world.getName());
		List<Double> d = Lists.newArrayList(new Double[] { loc.getX(),
				loc.getY(), loc.getZ() });
		config.set(key, d);

		wcm.saveConfig(world.getName());

	}

	public static String getLocationPath(LOCATIONS l) {
		String key = "location";
		switch (l) {
		case RED:
			key += ".spawn.red";
			break;
		case BLUE:
			key += ".spawn.blue";
			break;
		case GREEN:
			key += ".spawn.green";
			break;
		case YELLOW:
			key += ".spawn.yellow";
			break;
		case BOUNDS1:
			key += ".bounds1";
			break;
		case BOUNDS2:
			key += ".bounds2";
			break;
		}
		return key;
	}

}
