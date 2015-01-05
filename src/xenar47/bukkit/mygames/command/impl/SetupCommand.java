package xenar47.bukkit.mygames.command.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class SetupCommand extends MyGamesCommand {
	
	public SetupCommand(MyGames mygames) {
		super(mygames, "setup");
		
		this.setPermission("mygames.command.setup");
		this.setUsage("setup");
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
		
		mygames.toSetup(player);
		mm.setInSetup(player);
		
		return true;
	}

}