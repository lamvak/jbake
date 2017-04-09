package org.jbake.app.asset;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;

class AssetDirectoryCopier extends AssetCopier {
	public AssetDirectoryCopier(File path, AssetCopyingContext copyingContext) {
		super(path, copyingContext);
	}

	@Override
	protected void copy() {
		copyFiles();
		copyDirectories();
	}

	private void copyDirectories() {
		File[] assetsDirectories = assetFile.listFiles(File::isDirectory);
		copyAssetsFrom(assetsDirectories, directory -> new AssetDirectoryCopier(directory,
				copyingContext));
	}

	private void copyFiles() {
		File[] assetFiles = assetFile.listFiles(File::isFile);
		copyAssetsFrom(assetFiles, file -> new AssetFileCopier(file, copyingContext));
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
		copier.copyAsset();
	}
}
