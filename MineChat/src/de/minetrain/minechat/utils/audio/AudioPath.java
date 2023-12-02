package de.minetrain.minechat.utils.audio;

import java.net.URI;
import java.nio.file.Path;

/**
 * This Class is used to fill a GUI dropdown menu to select a audio path.
 * @author MineTrain/Justin, ZockiRR
 * @see {@link AudioManager}
 */
public class AudioPath {
	private final Path path;

	public AudioPath(String path) {
		this.path = Path.of(path);
	}
	
	public AudioPath(Path path) {
		this.path = path;
	}
	
	public AudioPath() {
		this.path = null;
	}
	

	/**
	 * @return {@link Path}
	 */
	public Path getPath(){
		return path;
	}
	
	/**
	 * @return {@link URI#toString()}
	 */
	public String getUri(){
		return isDummy() ? "" : path.toUri().toString();
	}
	
	public boolean isDummy(){
		return path==null;
	}
	
	@Override
	public String toString() {
		return path != null ? path.getFileName() + " - ("+path.subpath(1, path.getNameCount()).getParent()+")" : "->> no sound <--";//.toString().replace("\\", "/")
	}
	
}
