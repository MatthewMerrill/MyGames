package xenar47.bukkit.mygames.task;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;

public class CountdownTask implements Runnable {
	
	private final MyGames mygames;
	private final Game game;
	
	private boolean running = false;
	private BukkitTask task;
	
	private final int startCount;
	private int countDown;
	
	public CountdownTask(MyGames mygames, Game game, int countDown) {
		this.mygames = mygames;
		this.game = game;
		
		if (countDown < 1) {
			throw new IllegalArgumentException("Counter must be at least 1");
		} else {
			this.startCount = countDown;
			this.countDown = countDown;
		}
	}
	
	public void start() {
		countDown = startCount;
		if (!running) {
			task = Bukkit.getScheduler().runTaskTimer(mygames, this, 0, 20 * 5);
			running = true;
		}
	}
	
	public void stop() {
		if (running) {
			Bukkit.getScheduler().cancelTask(task.getTaskId());
			this.running = false;
		}
	}
	
	public void cancel() {
		stop();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void run() {
		countDown-= 5;
		
		ArrayList<UUID> players = game.getPlayers(); 
		
		game.getSidebarManager().countdown(countDown);
		
		if (countDown <= 0) {
			stop();
			if (players.size() >= game.getMinPlayers()) {
				game.startGame();
			} else {
				for (UUID uuid: players) {
					Bukkit.getPlayer(uuid).sendMessage(
							ChatColor.GRAY + "Unable to start " + ChatColor.stripColor(game.getName())
							+ ": Not enough players.");
				}
			}
		}
	}

}
