package org.jbake.app;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public abstract class AssetCopier {
	protected final File assetFile;
	protected final String sourcePathPrefix;
	protected final File destination;
	protected boolean ignoreHidden;

	protected final LinkedList<IOException> errors = new LinkedList<>();

	protected AssetCopier(File assetFile, String sourcePathPrefix, File destination, boolean
			ignoreHidden) {
		this.assetFile = assetFile;
		this.sourcePathPrefix = sourcePathPrefix;
		this.destination = destination;
		this.ignoreHidden = ignoreHidden;
	}
	protected abstract void copy();

	public LinkedList<IOException> getErrors() {
		return errors;
	}

	public void copyAsset() {
		if (currentFileShouldBeCopied()) {
			copy();
		}
	}

	protected boolean currentFileShouldBeCopied() {
		return !(ignoreHidden && assetFile.isHidden());
	}
}
