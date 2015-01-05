package xenar47.bukkit.mygames.command.impl;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.ConfigManager;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class SetSpawnCommand extends MyGamesCommand {
	
	List<String> worldKeywords;// = new ArrayList<String>();
	List<String> locKeywords;// = new ArrayList<String>();
	List<String> disableKeywords;// = new ArrayList<String>();
	
	public SetSpawnCommand(MyGames mygames) {
		super(mygames, "setspawn");
		
		this.setPermission("mygames.command.setspawn");
		this.setUsage("setspawn [world|location|disable]");
		
		worldKeywords = Arrays.asList(new String[]{"world", "wor", "w"});
		locKeywords = Arrays.asList(new String[]{"location", "loc", "l"});
		disableKeywords = Arrays.asList(new String[]{"disable", "false", "stop", "null", "default", ""});
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		Player player = getPlayerFromSender(sender);
		if (player == null)
			return true;
		
		if (args.length == 1) {
			if (worldKeywords.contains(args[0])) {
				setWorld(player.getWorld());
				setLoc(null);
				player.sendMessage(ChatColor.GRAY + "MyGames spawn set to world " + player.getWorld().getName() + ".");
				return true;
				
			} else if (locKeywords.contains(args[0])) {
				setWorld(player.getWorld());
				setLoc(player.getLocation());
				player.sendMessage(ChatColor.GRAY + "MyGames spawn set to your location.");
				return true;
			} else if (disableKeywords.contains(args[0])) {
				setWorld(null);
				setLoc(null);
				player.sendMessage(ChatColor.GRAY + "MyGames spawn defaulting to spawn location of main world.");
				return true;
			}
		}
		
		sender.sendMessage(ChatColor.RED + "Invalid arguments.");
		sender.sendMessage(ChatColor.RED + "disable - disables custom spawn; defaults to main world's spawn");
		sender.sendMessage(ChatColor.RED + "world - sets the spawn location to your current world's spawn");
		sender.sendMessage(ChatColor.RED + "location - sets the spawn location to where you are currently standing");
		return true;
	}
	
	private void setWorld(World w) {
		if (w != null)
			mygames.getConfig().set(ConfigManager.SPAWN_WORLD, w.getName());
		mygames.getConfig().set(ConfigManager.SPAWN_WORLD_ENABLED, w != null);
		mygames.saveConfig();
	}
	private void setLoc(Location loc) {
		if (loc != null)
			mygames.getConfig().set(ConfigManager.SPAWN_LOCATION, loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch());
		mygames.getConfig().set(ConfigManager.SPAWN_LOCATION_ENABLED, loc != null);
		mygames.saveConfig();
	}

}