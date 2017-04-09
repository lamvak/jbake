package org.jbake.app;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;

class AssetDirectoryCopier extends AssetCopier {
	AssetDirectoryCopier(File assetFile, String sourcePathFragment, File destination, boolean
			ignoreHidden) {
		super(assetFile, sourcePathFragment, destination, ignoreHidden);
	}

	@Override
	public void copy() {
		copyFiles();
		copyDirectories();
	}

	private void copyDirectories() {
		File[] assetsDirectories = assetFile.listFiles(File::isDirectory);
		copyAssetsFrom(assetsDirectories, directory -> new AssetDirectoryCopier(directory,
				sourcePathPrefix, destination, ignoreHidden));
	}

	private void copyFiles() {
		File[] assetFiles = assetFile.listFiles(File::isFile);
		copyAssetsFrom(assetFiles, file -> new AssetFileCopier(file, sourcePathPrefix, destination, ignoreHidden));
	}

	private void copyAssetsFrom(File[] assetsFiles, Function<File, AssetCopier> copierFactory) {
		if (assetsFiles != null) {
			Arrays.sort(assetsFiles);
			for (File assetFile : assetsFiles) {
				copyAsset(assetFile, copierFactory);
			}
		}
	}

	private void copyAsset(File assetFile, Function<File, AssetCopier> copierFactory) {
		AssetCopier copier = copierFactory.apply(assetFile);
		copier.copy();
		errors.addAll(copier.getErrors());
	}
}
