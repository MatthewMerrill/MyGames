package xenar47.bukkit.mygames.command.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.Utils;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class ListCommand extends MyGamesCommand {
	
	public ListCommand(MyGames mygames) {
		super(mygames, "list");
		
		this.setPermission("mygames.command.list");
		this.setUsage("list");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		sender.sendMessage(ChatColor.GRAY + "Games Running: " +
				Utils.list(mygames.getLobbyMgr().getGames(), ChatColor.GRAY));
		
		return true;
	}

}