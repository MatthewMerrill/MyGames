package xenar47.bukkit.mygames.command.world;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.MyGamesCommand;

public class AddWorldCommand extends MyGamesCommand {

	public AddWorldCommand(MyGames mygames) {
		super(mygames, "add");
		
		this.setPermission("mygames.command.world.add");
		this.setUsage("world add [world name]");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		String world = ""; 
		if (args.length >= 1) {
			world = args[0];
		} else {
			if (sender instanceof Player) {
				world = ((Player)sender).getWorld().getName();
			} else {
				sender.sendMessage(ChatColor.RED + "If you are not a player, you must declare the world."
						+ " /mygames world add [world name]");
			}
			return true;
		}
		
		mygames.getWorldMgr().addToList(world);
		
		return true;
	}

}
