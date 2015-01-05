package xenar47.bukkit.mygames.command.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class PlayCommand extends MyGamesCommand {
	
	public PlayCommand(MyGames mygames) {
		super(mygames, "play");
		
		this.setPermission("mygames.command.play");
		this.setUsage("play");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		Player player = getPlayerFromSender(sender);
		if (player == null)
			return true;
		
		MetadataManager mm = mygames.getMetaMgr();
		if (mm.getMode(player) == MetadataManager.OTHER_GAME) {
			player.sendMessage(ChatColor.RED + "Sorry dude, I'm trying to be nice to other plugins;"
					+ " I can't let you use that command right now."
					+ " Leave the game you are currently attached to in order to play."
					+ "(One way to achieve this could be reconnecting? :/)");
			return true;
		}

		mygames.toLobby(player);
		mm.setInLobby(player);
		
		return true;
	}

}