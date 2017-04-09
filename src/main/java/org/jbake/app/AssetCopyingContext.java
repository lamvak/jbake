package org.jbake.app;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

class AssetCopyingContext {
	private final boolean ignoreHidden;
	private final File destination;
	private final String sourcePathFragment;
	private final LinkedList<Throwable> errors;

	public AssetCopyingContext(boolean ignoreHidden, File destination, String sourcePathPrefix) {
		this.ignoreHidden = ignoreHidden;
		this.destination = destination;
		this.sourcePathFragment = sourcePathPrefix;
		this.errors = new LinkedList<>();
	}

	public boolean isIgnoreHidden() {
		return ignoreHidden;
	}

	public File getDestination() {
		return destination;
	}

	public LinkedList<Throwable> getErrors() {
		return errors;
	}

	public File getDestinationFor(File assetFile) {
		String sourcePath = assetFile.getPath();
		String destinationPath = destination.getPath();
		return new File(sourcePath.replace(sourcePathFragment, destinationPath));
	}

	public void addError(IOException e) {
		errors.add(e);
	}
}
