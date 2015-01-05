package xenar47.bukkit.mygames.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.world.AddWorldCommand;
import xenar47.bukkit.mygames.command.world.JoinWorldCommand;

public class WorldCommand extends MyGamesCommand {

	CommandMap map;
	
	protected WorldCommand(MyGames mygames) {
		super(mygames, "world");
		
		map = new CommandMap(mygames){

			@Override
			public void defaultCommand(CommandSender sender) {
				sender.sendMessage(ChatColor.GRAY + "/mygames world help");
			}
			
		};
		
		map.register("join", new JoinWorldCommand(mygames));
		map.register("add", new AddWorldCommand(mygames));
	}

	@Override
	public boolean execute(CommandSender sender, String label,
			String[] args) {
		return map.onCommand(sender, this, label, args);
	}

}
