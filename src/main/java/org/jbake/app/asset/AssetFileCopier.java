package org.jbake.app.asset;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

class AssetFileCopier extends AssetCopier {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetFileCopier.class);
	private StringBuilder sb;

	public AssetFileCopier(File assetFile, AssetCopyingContext copyingContext) {
		super(assetFile, copyingContext);
		sb = new StringBuilder();
	}

	@Override
	protected void copy() {
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
		File destinationFile = copyingContext.getDestinationFor(assetFile);
		FileUtils.copyFile(assetFile, destinationFile);
		reportCopyOperationDone();
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
		copyingContext.addError(e);
	}
}
