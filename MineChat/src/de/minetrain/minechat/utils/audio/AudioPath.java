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
		if(path == null){
			this.path = null;
			return;
		}
		
		this.path = Path.of(path.startsWith("data\\sounds") ? path : "data\\sounds\\"+path);
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
	
	public String getFilePathAsString(){
		return getPath().subpath(2, getPath().getNameCount()).getParent().toString()+"\\"+getPath().getFileName();
	}
	
	@Override
	public String toString() {
		String string = path != null ? path.getFileName() + " - ("+path.subpath(2, path.getNameCount()).getParent()+")" : "->> no sound <--";//.toString().replace("\\", "/")
		string = string.substring(0, string.length() > 43 ? 43 : string.length());
		return string;
	}
	
}
