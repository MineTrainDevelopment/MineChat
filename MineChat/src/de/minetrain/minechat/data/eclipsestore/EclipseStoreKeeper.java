package de.minetrain.minechat.data.eclipsestore;

import java.nio.file.Path;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipseStoreKeeper {

	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreKeeper.class);

	private static EclipseStoreKeeper instance;

	private EmbeddedStorageManager storageManager;
	private EclipseStoreRoot root;


	public static void init() {
		if(instance != null) {
			LOG.warn("EclipseStoreKeeper is already initialized and will be recreated.");
		}
		instance = new EclipseStoreKeeper();
	}

	public static EclipseStoreKeeper instance() {
		return instance;
	}

	private EclipseStoreKeeper() {
		storageManager = EmbeddedStorage.start(Path.of("data", "database", "eclipse_store"));

		root = (EclipseStoreRoot) storageManager.root();

		if(root == null){
			root = new EclipseStoreRoot();
			storageManager.setRoot(root);
			storageManager.storeRoot();
			root.persister = storageManager;
		}
	}

	public static EmbeddedStorageManager storeManager() {
		return instance().storageManager;
	}

	public static EclipseStoreRoot storeRoot() {
		return instance().root;
	}
}
