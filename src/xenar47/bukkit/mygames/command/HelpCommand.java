package xenar47.bukkit.mygames.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.Utils;

public class HelpCommand extends MyGamesCommand {
	
	CommandMap cmdMap;
	
	public HelpCommand(MyGames mygames, CommandMap cmdMap) {
		super(mygames, "help");
		
		this.cmdMap = cmdMap;
		this.setPermission("mygames.command.help");
		this.setUsage("help (\"all\")");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel,
			String[] args) {
		
		ArrayList<String> commands = new ArrayList<String>(cmdMap.commandNames());
		
		String prefix = "Registered Commands: ";
		boolean a = false;
		if (!((args.length > 0) && (args[0].equalsIgnoreCase("all")))) {
			
			prefix = "Commands available to you: ";
			
			for (String string : cmdMap.commandNames()) {
				String perm = cmdMap.getCommand(string).getPermission();
				if ((perm != null)&&(!sender.hasPermission(perm))) {
					commands.remove(string);
					a = true;
				}
			}
		}
		
		//commands.sort(null);
		sender.sendMessage(ChatColor.GRAY + prefix + Utils.list(commands, ChatColor.GRAY, ChatColor.BLUE));
		if (a)
			sender.sendMessage(ChatColor.GRAY + "To see all commands, use the argument \""
					+ ChatColor.BLUE + "all" + ChatColor.GRAY +"\".");
		
		return true;
	}

}
