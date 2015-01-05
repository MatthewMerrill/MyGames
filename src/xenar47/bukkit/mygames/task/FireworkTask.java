package xenar47.bukkit.mygames.task;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;

public class FireworkTask implements Runnable {
	
	private final MyGames mygames;
	private final Game game;
	
	private boolean running = false;
	private BukkitTask task;
	
	private int tickDelay;
	private int ticks = 0;
	
	boolean weaponsGiven;
	
	public FireworkTask(MyGames mygames, Game game) {
		this.mygames = mygames;
		this.game = game;
		
		tickDelay = 5;
	}
	
	public void start() {
		ticks = 0;
		if (!running) {
			task = Bukkit.getScheduler().runTaskTimer(mygames, this, 0, tickDelay);
			running = true;
		}
	}
	
	public void stop() {
		if (running) {
			Bukkit.getScheduler().cancelTask(task.getTaskId());
			this.running = false;
		}
	}
	
	/**
	 * Same as stop().
	 * Don't know why I added it, but I did so it has to do something... I think... Maybe it used to override BukkitTask.cancel()? idk.
	 *  - MattMerr47
	 */
	public void cancel() {
		stop();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void run() {
		ticks += tickDelay;
		
		if ((ticks / 20.0) <= 10) {
			//Make Firework
		} else {
			//Return players
		}
	}
}
