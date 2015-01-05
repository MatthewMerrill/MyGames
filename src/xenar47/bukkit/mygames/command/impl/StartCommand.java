package xenar47.bukkit.mygames.command.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class StartCommand extends MyGamesCommand {
	
	public StartCommand(MyGames mygames) {
		super(mygames, "start");
		
		this.setPermission("mygames.command.start");
		this.setUsage("start [game name]");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		Game gm = getGame(sender, args, 0);
		if (gm == null)
			return true;
		
		if (gm.isRunning()) {
			sender.sendMessage(ChatColor.RED + gm.getName() + " is already running!");
		} else {
			gm.startGame();
		}
		
		return true;
	}

}