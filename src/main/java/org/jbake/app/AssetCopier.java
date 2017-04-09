package org.jbake.app;

import java.io.File;

public abstract class AssetCopier {
	protected final File assetFile;
	protected final AssetCopyingContext copyingContext;

	public AssetCopier(File assetFile, AssetCopyingContext copyingContext) {
		this.assetFile = assetFile;
		this.copyingContext = copyingContext;
	}

	protected abstract void copy();


	public void copyAsset() {
		if (currentFileShouldBeCopied()) {
			copy();
		}
	}

	protected boolean currentFileShouldBeCopied() {
		return !(copyingContext.isIgnoreHidden() && assetFile.isHidden());
	}
}
