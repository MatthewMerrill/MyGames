package xenar47.bukkit.mygames.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;

public abstract class MyGamesCommand extends Command {
	
	protected MyGames mygames;
	
	public MyGamesCommand(MyGames mygames, String name) {
		super(name);
		this.mygames = mygames;
	}
	
	public Player getPlayerFromSender(CommandSender sender) {
		if (sender instanceof Player)
			return (Player) sender;
		
		sender.sendMessage(ChatColor.RED
				+ "You must be a player to use this command.");
		
		return null;
	}
	
	public Game getGame(CommandSender sender, String[] args, int argIndex) {
		if (argIndex >= args.length) {
			sender.sendMessage(ChatColor.RED + "/mygames " + this.getUsage());
			return null;
		}
		
		return getGame(sender, args[argIndex]);
	}
	
	public Game getGame(CommandSender sender, String gameName) {
		Game gm = mygames.getLobbyMgr().getGame(gameName);
		if (gm == null) {
			sender.sendMessage(mygames.getChatManager().gameNotFound(gameName));
		}
		
		return gm;
	}

}
