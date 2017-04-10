package org.jbake.app.render;

import java.io.File;
import java.util.Map;

public class ModelRenderingConfig extends AbstractRenderingConfig {
	private final Map<String, Object> model;

	public ModelRenderingConfig(File path, String name, String template, Map<String, Object> model) {
		super(path, name, template);
		this.model = model;
	}

	@Override
	public Map<String, Object> getModel() {
		return model;
	}
}
