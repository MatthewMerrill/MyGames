package xenar47.bukkit.mygames.command.world;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class JoinWorldCommand extends MyGamesCommand {

	public JoinWorldCommand(MyGames mygames) {
		super(mygames, "join");
		
		this.setPermission("mygames.command.world.join");
		this.setUsage("join [world name]");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Not enough arguments! /mygames world join [worldname]");
			return true;
		}
		
		getPlayerFromSender(sender).teleport(Bukkit.getServer().getWorld(args[0]).getSpawnLocation());
				
		return true;
	}

}
