package org.jbake.app.render;

import java.io.File;

abstract class AbstractRenderingConfig implements RenderingConfig {
	protected final File path;
	protected final String name;
	protected final String template;

	public AbstractRenderingConfig(File path, String name, String template) {
		super();
		this.path = path;
		this.name = name;
		this.template = template;
	}

	@Override
	public File getPath() {
		return path;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTemplate() {
		return template;
	}

}
