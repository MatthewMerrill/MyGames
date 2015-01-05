package xenar47.bukkit.mygames.chat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xenar47.bukkit.mygames.Utils;
import xenar47.bukkit.mygames.api.Game;

public class DefaultChatManager implements ChatManager {
	
	private static String prefix(Game gm) {
		return ChatColor.GREEN + "[" + ChatColor.stripColor(gm.getName()) + "]";
	}
	private static String prefix() {
		return ChatColor.GREEN + "[MyGames]";
	}
	
	@Override
	public String joinServer(Player player) {
		return ChatColor.YELLOW + player.getDisplayName()
				+ ChatColor.YELLOW + " has entered the arena!";
	}
	
	@Override
	public String leaveServer(Player player) {
		return ChatColor.YELLOW + player.getDisplayName()
				+ ChatColor.YELLOW + " has left the arena.";
	}
	
	@Override
	public String playerDeath(Player player) {
		return ChatColor.YELLOW + player.getDisplayName()
				+ ChatColor.YELLOW + " has perished.";
	}
	
	@Override
	public String playerDeath(Player player, Player killer) {
		return ChatColor.YELLOW + player.getDisplayName()
				+ ChatColor.YELLOW + " has been slaughtered by " + killer.getDisplayName() +".";
	}
	
	@Override
	public String joinLobbySuccess(Game gm) {
		return prefix(gm)
				+ ChatColor.GRAY + "You have joined the wait list. "
				+ "See sidebar for information on when the game will start.";
	}
	
	@Override
	public String leaveLobby(Game gm) {
		return prefix(gm)
				+ ChatColor.GRAY + "You have left the wait list.";
	}
	
	@Override
	public String joinLobbyAlreadyJoined(Game gm) {
		return prefix(gm)
				+ ChatColor.RED + "You are already registered for this game!";
	}
	
	@Override
	public String joinLobbyFull(Game gm) {
		return prefix(gm)
				+ ChatColor.RED + "This game is full!";
	}
	
	@Override
	public String joinLobbyIngame(Game gm) {
		return prefix(gm)
				+ ChatColor.RED + "You are in another game!";
	}
	
	@Override
	public String joinLobbyInProgress(Game gm) {
		return prefix(gm)
				+ ChatColor.RED + "You cannot join this game while it is in session!";
	}
	
	@Override
	public String gameNotFound(String gameName) {
		return prefix()
				+ ChatColor.RED + "Could not find game \"" + ChatColor.stripColor(gameName) + "\". What games are running? /mygames list";
	}
	
	@Override
	public String gameHasNoWorlds(Game game) {
		return prefix()
				+ ChatColor.RED + "Could not begin game \"" + game.getName() + "\": No compatible worlds.";
	}
	
	@Override
	public String gameStart(Game gm) {
		return prefix()
				+ ChatColor.GRAY + ChatColor.stripColor(gm.getName()) + " has begun. Prepare to recieve weapons!";
	}
	
	@Override
	public String gameOver(Game gm, ArrayList<String> winners) {
		String message = prefix(gm);
		
		if (winners != null) {		
			if (winners.size() >= 1) {
				message += Utils.list(winners, ChatColor.GRAY, ChatColor.BLUE);
				message += ChatColor.GRAY + " took"/* + ((winners.size()==1)?"":"s")*/ + " the victory!";
				Bukkit.broadcastMessage(message);
			} else {
				message += ChatColor.GRAY + "All participants have died... No Winners!";
			}
		} else {
			message += ChatColor.GRAY + "Ended in mysterious circumstances... No Winners!";
		}
		
		return (message);
	}
	
	@Override
	public String commandNotRegistered() {
		return prefix()
				+ ChatColor.RED + "That command is not registered!";
	}
	
	@Override
	public String actionNotAllowed() {
		return ChatColor.GRAY.toString()
				+ ChatColor.ITALIC.toString() + " I'm sorry, but I'm afraid I can't let you do that.";
	}
}
