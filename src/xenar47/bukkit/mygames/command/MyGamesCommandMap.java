package xenar47.bukkit.mygames.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.command.impl.JoinCommand;
import xenar47.bukkit.mygames.command.impl.LeaveCommand;
import xenar47.bukkit.mygames.command.impl.ListCommand;
import xenar47.bukkit.mygames.command.impl.PlayCommand;
import xenar47.bukkit.mygames.command.impl.SetSpawnCommand;
import xenar47.bukkit.mygames.command.impl.SetupCommand;
import xenar47.bukkit.mygames.command.impl.StartCommand;
import xenar47.bukkit.mygames.command.impl.StopCommand;

public class MyGamesCommandMap extends CommandMap {

	public MyGamesCommandMap(MyGames mygames) {
		super(mygames);
		
		register("join", new JoinCommand(mygames));
		register("leave", new LeaveCommand(mygames));
		register("start", new StartCommand(mygames));
		register("stop", new StopCommand(mygames));
		register("play", new PlayCommand(mygames));
		register("setup", new SetupCommand(mygames));
		register("list", new ListCommand(mygames));
		register("setspawn", new SetSpawnCommand(mygames));
		
		register("world", new WorldCommand(mygames));
	}

	@Override
	public void defaultCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY + "New to MyGames? Type \"/mygames help\" for a list of commands!");
	}

}
