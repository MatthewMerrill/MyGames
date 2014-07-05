package xenar47.bukkit.mygames.world;

public class WorldType {

	private String typeName;
	private boolean team_based;
	private boolean has_capture_point;
	private int max_entries;

	public WorldType(boolean team_based, boolean has_capture_point,
			int max_entries) {

		this.team_based = team_based;
		this.has_capture_point = has_capture_point;
		this.max_entries = max_entries;

	}

}
