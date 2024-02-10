package de.minetrain.minechat.data.eclipsestore;

import java.nio.file.Path;
import java.util.Objects;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

public class EclipseStoreTest {
	private static EmbeddedStorageManager storageManager;
	private static EclipseStoreRoot root;
	
	public EclipseStoreTest() {
		storageManager = EmbeddedStorage.start(Path.of("data", "database", "eclipse_store"));
		
		root = getStoreRoot();
		
		if(root == null){
			root = new EclipseStoreRoot();
			
			storageManager.setRoot(root);
			storageManager.storeRoot();
			root.persister = storageManager;
		}
		
	}

	public static EclipseStoreRoot getStoreRoot() {
		return (EclipseStoreRoot) storageManager.root();
	}

}
