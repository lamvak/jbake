package org.jbake.app;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Deals with assets (static files such as css, js or image files).
 *
 * @author Jonathan Bullock <jonbullock@gmail.com>
 */
public class Asset {

	private static final Logger LOGGER = LoggerFactory.getLogger(Asset.class);

	private File destination;
	private final List<Throwable> errors = new LinkedList<Throwable>();
	private final boolean ignoreHidden;
	private String sourcePathFragment;

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
		File[] assetsFiles = listAssets(path);
		if (assetsFiles != null) {
			copyAssetsFrom(assetsFiles);
		}
	}

	private void copyAssetsFrom(File[] assetFiles) {
		Arrays.sort(assetFiles);
		for (File assetFile : assetFiles) {
			copyAssetFromFile(assetFile);
		}
	}

	private void copyAssetFromFile(File assetFile) {
		if (assetFile.isFile()) {
			copyAssetFromActualFile(assetFile);
		}
		if (assetFile.isDirectory()) {
			copy(assetFile);
		}
	}

	private void copyAssetFromActualFile(File assetFile) {
		StringBuilder sb = new StringBuilder();
		sb.append("Copying [")
				.append(assetFile.getPath())
				.append("]...");
		File destFile = new File(assetFile.getPath().replace(sourcePathFragment, destination.getPath()));
		try {
			FileUtils.copyFile(assetFile, destFile);
			sb.append("done!");
			LOGGER.info(sb.toString());
		} catch (IOException e) {
			sb.append("failed!");
			LOGGER.error(sb.toString(), e);
			e.printStackTrace();
			errors.add(e);
		}
	}

	private File[] listAssets(File path) {
		return path.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !ignoreHidden || !file.isHidden();
			}
		});
	}

	public List<Throwable> getErrors() {
		return new ArrayList<Throwable>(errors);
	}

}
