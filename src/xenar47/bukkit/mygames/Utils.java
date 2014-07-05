package xenar47.bukkit.mygames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.MetadataValue;

public class Utils {
	
	public static String list(ArrayList<String> items, ChatColor baseColor, ChatColor itemColor) {
		String list = "";
		
		if (items.size() == 2) {
			list = itemColor + items.get(0) + baseColor + " and "
					+ itemColor + items.get(1);
		} else if (items.size() >= 1) {
			for (int i = 0; i < items.size(); i++) {
				list +=  baseColor + ((i==0)?"":", ")
						+ (((i==items.size()-1) && (i != 0))?"and ":"") 
						+ itemColor + items.get(i);
			}
		}
		
		return list;
	}
	
	public static String list(ArrayList<String> items, ChatColor baseColor) {
		return list(items, baseColor, ChatColor.RESET);
	}
	
	public static String list(ArrayList<String> items) {
		return list(items, ChatColor.RESET, ChatColor.RESET);
	}
	
	public static boolean hasMetadataValue(List<MetadataValue> metas, Object value) {
		for (MetadataValue meta : metas) {
			try {
				Object metaValue = meta.value();
				if (metaValue == value || metaValue.equals(value)) {
					return true;
				}
			} catch (Exception e) {
				continue;
			}
		}
		return false;
	}
	
	public static void fakeDeath(Location loc) {
		Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		villager.playEffect(EntityEffect.HURT);
		villager.playEffect(EntityEffect.DEATH);
		villager.setHealth(0);
	}

}
