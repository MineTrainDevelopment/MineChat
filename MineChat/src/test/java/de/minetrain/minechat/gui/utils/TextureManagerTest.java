package de.minetrain.minechat.gui.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class TextureManagerTest {

	@BeforeAll
	static void initJavaFX() {
		try {
			Platform.startup(() -> {});
		} catch (IllegalStateException e) {
			// Toolkit already initialized
		}
	}

	@AfterAll
	static void teardownJavaFX() {
		Platform.exit();
	}

	public static final byte[] NETSCAPE2_0 = "NETSCAPE2.0".getBytes();

	@Test
	void testGifLoopEnablement() throws IOException {
		byte[] data = readImageData("dance_no_loop.gif");
		Image image = new Image(new ByteArrayInputStream(data));
		assertFalse(image.isError(), "Source gif is corrupt: " + image.getException());
		assertFalse(isLoopEnabled(data), "Source gif should not have loop enabled");

		TextureManager.enableLoop(data);

		image = new Image(new ByteArrayInputStream(data));
		assertFalse(image.isError(), "Modified gif is corrupt: " + image.getException());
		assertTrue(isLoopEnabled(data), "Loop was not enabled");
	}

	@Test
	void testGifValidation() throws IOException {
		byte[] data = readImageData("dance_corrupt_lzw.gif");
		Image image = new Image(new ByteArrayInputStream(data));
		assertTrue(image.isError(), "Source image is not corrupt");

		data = TextureManager.validate(data);

		image = new Image(new ByteArrayInputStream(data));
		assertFalse(image.isError(), "Modified image is still corrupt: " + image.getException());
		assertTrue(isLoopEnabled(data), "Loop was not enabled");
	}

	private static byte[] readImageData(String name) throws IOException {
		try (InputStream rs = TextureManagerTest.class.getResourceAsStream("/" + name)) {
			return rs.readAllBytes();
		}
	}

	private static boolean isLoopEnabled(byte[] gifData) {
		int offset = 13 + ((gifData[10] & 0b10000000) != 0 ? (2 << (gifData[10] & 0b00000111)) * 3 : 0);
		for (int i = offset; i < gifData.length; i++) {
			if (gifData[i] == (byte) 0x21 && gifData[i + 1] == (byte) 0xFF) {
				byte size = gifData[i + 2];
				if (size == NETSCAPE2_0.length && Arrays.equals(NETSCAPE2_0, 0, size, gifData, i + 3, i + 3 + size)) {
					int pos = i + 3 + size;
					return pos + 2 < gifData.length && gifData[pos] == 3 && gifData[pos + 1] == 1 && gifData[pos + 2] == 0;
				}
			}
		}
		return false;
	}
}
