package xenar47.bukkit.mygames.effects;

import org.bukkit.Effect;
import org.bukkit.Location;

public class ParticleEffects {
	
	public static void line(Effect effect, int data, Location loc1, Location loc2) {
		Location pos = loc1.clone();
		
		double repeats = (10.0*loc2.distance(loc1));
		Location dPos = loc2.subtract(loc1).multiply(1/repeats);
		
		for (int i = 0; i < repeats; i++) {
			//loc1.getWorld().playEffect(pos, effect, data);
			//loc1.getWorld().//.spawnParticle("reddust", posX, posY, posZ, 0.0D /*red*/, 1.0D /*green*/, 0.0D /*blue*/);
			pos = pos.add(dPos);
		}		
	}

}
