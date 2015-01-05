package xenar47.bukkit.mygames.api;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import xenar47.bukkit.mygames.MyGames;

public class SidebarManager {
		
	public static void updateLobbyBoard(MyGames mygames) {
		//TODO: Configurable lobby board?
	}
	
	private final Game game;
	
	protected final Scoreboard scoreboard;
	protected Objective sidebar;
	
	public SidebarManager(Game game) {
		this.game = game;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		resetSidebar();
	}
	
	public void playersNeeded() {
		resetSidebar();
		
		sidebar.getScore("Players Needed:").setScore(game.getPlayersNeeded());
	}
	
	public void warmup(int secondsLeft) {
		resetSidebar();
		
		sidebar.getScore("Warmup").setScore(secondsLeft);
		
		update();
	}
	
	public void scores() {
		resetSidebar();
		
		ArrayList<GameScore> scores = game.getSideScores();
		if (scores != null) {
			int count = 0;
			for (GameScore score : scores) {
				if (count >= 8)
					break;
				sidebar.getScore(score.name).setScore(score.score);
				count++;
			}
		}
		
		update();
	}
	
	public void timeRemaining(int secondsLeft) {
		resetSidebar();
		
		sidebar.getScore("Time Remaining:").setScore(secondsLeft);
		
		update();
	}
	
	public void resetSidebar() {
		if (sidebar != null && sidebar.isModifiable())
			sidebar.unregister();
		
		sidebar = scoreboard.registerNewObjective("sidebar", "dummy");

		sidebar.setDisplayName(game.getUniqueName());
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void update() {
		//TODO: Do I need anything here?
	}
	
	public void show() {
		for (UUID uuid : game.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);	
			if (player.isOnline())
				player.setScoreboard(scoreboard);
		}
	}

	public void hide() {
		scoreboard.clearSlot(DisplaySlot.BELOW_NAME);
		scoreboard.clearSlot(DisplaySlot.PLAYER_LIST);
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		
		update();
	}

	public void countdown(int countDown) {
		resetSidebar();
		
		sidebar.getScore("Game Starting:").setScore(countDown);
		
		update();
	}

}
