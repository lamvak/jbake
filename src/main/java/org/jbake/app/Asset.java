package org.jbake.app;

import org.apache.commons.configuration.CompositeConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Deals with assets (static files such as css, js or image files).
 *
 * @author Jonathan Bullock <jonbullock@gmail.com>
 */
public class Asset {
	private final boolean ignoreHidden;

	private File destination;
	private String sourcePathFragment;
	private ArrayList<Throwable> errors;

	/**
	 * Creates an instance of Asset.
	 */
	public Asset(File source, File destination, CompositeConfiguration config) {
		this.destination = destination;
		ignoreHidden = config.getBoolean(ConfigUtil.Keys.ASSET_IGNORE_HIDDEN, false);
		sourcePathFragment = source.getPath() + File.separator + config.getString(ConfigUtil.Keys.ASSET_FOLDER);
	}

	/**
	 * Copy all files from supplied path.
	 *
	 * @param path The starting path
	 */
	public void copy(File path) {
		AssetDirectoryCopier assetDirectoryCopier = new AssetDirectoryCopier(path,
				sourcePathFragment, destination, ignoreHidden);
		assetDirectoryCopier.copyAsset();
		errors = new ArrayList<>(assetDirectoryCopier.getErrors());
	}

	// why not concrete list of IOExceptions?
	public List<Throwable> getErrors() {
		return errors;
	}

}
