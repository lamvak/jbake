package org.jbake.app.asset;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;

import java.io.File;
import java.util.List;

/**
 * Deals with assets (static files such as css, js or image files).
 *
 * @author Jonathan Bullock <jonbullock@gmail.com>
 */
public class Asset {

	private final File source;
	private final File destination;
	private final CompositeConfiguration config;
	private AssetCopyingContext copyingContext;

	/**
	 * Creates an instance of Asset.
	 */
	public Asset(File source, File destination, CompositeConfiguration config) {
		this.source = source;
		this.destination = destination;
		this.config = config;
	}

	/**
	 * Copy all files from supplied path.
	 *
	 * @param path The starting path
	 */
	public void copy(File path) {
		createCopyingContext();
		AssetDirectoryCopier assetDirectoryCopier = new AssetDirectoryCopier(path, copyingContext);
		assetDirectoryCopier.copyAsset();
	}

	// why not concrete list of IOExceptions?
	public List<Throwable> getErrors() {
		return copyingContext.getErrors();
	}

	private void createCopyingContext() {
		boolean ignoreHidden = config.getBoolean(ConfigUtil.Keys.ASSET_IGNORE_HIDDEN, false);
		String sourcePathFragment = source.getPath() + File.separator + config.getString(ConfigUtil.Keys.ASSET_FOLDER);
		copyingContext = new AssetCopyingContext(ignoreHidden, destination, sourcePathFragment);
	}
}
