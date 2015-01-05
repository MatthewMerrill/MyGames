package xenar47.bukkit.mygames.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class LeaveCommand extends MyGamesCommand {
	
	public LeaveCommand(MyGames mygames) {
		super(mygames, "leave");
		
		this.setPermission("mygames.command.leave");
		this.setUsage("leave [game name]");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		Player player = getPlayerFromSender(sender);
		if (player == null)
			return true;
		
		Game gm = getGame(player, args, 0);
		if (gm == null)
			return true;
		
		gm.leaveGame(player);
		
		return true;
	}

}
