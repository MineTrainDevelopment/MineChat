package de.minetrain.minechat.gui.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/// An InputStream that lazily retrieves and validates data from a URL when read from.
/// If the data is a GIF, it will be validated to ensure it can be properly displayed while still using JavaFX Image background loading.
public class LazyGifValidatingInputStream extends InputStream {

	private String url;
	private InputStream delegate;

	public LazyGifValidatingInputStream(String url) {
		this.url = url;
	}

	private InputStream retrieveDelegate() throws IOException {
		if (delegate == null) {
			byte[] imageData = TextureManager.downloadImageData(url);
			if (TextureManager.isGif(imageData)) {
				imageData = TextureManager.validate(imageData);
			}
			delegate = new ByteArrayInputStream(imageData);
		}
		return delegate;
	}

	@Override
	public int read() throws IOException {
		return retrieveDelegate().read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return retrieveDelegate().read(b, off, len);
	}

	@Override
	public int available() throws IOException {
		return retrieveDelegate().available();
	}

	@Override
	public void close() {
		// No resources to close since the delegate is lazily initialized and backed by a byte array
	}
}
