package org.jbake.app;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

class AssetFileCopier extends AssetCopier {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetFileCopier.class);
	private StringBuilder sb;

	AssetFileCopier(File assetFile, String sourcePathFragment, File destination, boolean
			ignoreHidden) {
		super(assetFile, sourcePathFragment, destination, ignoreHidden);
		sb = new StringBuilder();
	}

	@Override
	public void copy() {
		reportCopyingOfAssetFile();
		tryCopyFile();
	}

	private void tryCopyFile() {
		try {
			copyFile();
		} catch (IOException e) {
			reportCopyOperationFailed(e);
		}
	}

	private void copyFile() throws IOException {
		FileUtils.copyFile(assetFile, destinationOfCopiedFile());
		reportCopyOperationDone();
	}

	private File destinationOfCopiedFile() {
		String sourcePath = assetFile.getPath();
		String destinationPath = destination.getPath();
		return new File(sourcePath.replace(sourcePathPrefix, destinationPath));
	}

	private void reportCopyingOfAssetFile() {
		sb.append("Copying [")
				.append(assetFile.getPath())
				.append("]...");
	}

	private void reportCopyOperationDone() {
		sb.append("done!");
		LOGGER.info(sb.toString());
	}

	private void reportCopyOperationFailed(IOException e) {
		sb.append("failed!");
		LOGGER.error(sb.toString(), e);
		e.printStackTrace();
		errors.add(e);
	}
}
