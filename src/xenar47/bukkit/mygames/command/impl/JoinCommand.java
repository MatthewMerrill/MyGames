package xenar47.bukkit.mygames.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class JoinCommand extends MyGamesCommand {
	
	public JoinCommand(MyGames mygames) {
		super(mygames, "join");
		
		this.setPermission("mygames.command.join");
		this.setUsage("join [game name]");
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
		
		if (gm.canJoin(player))
			gm.joinGame(player);
		
		return true;
	}

}
