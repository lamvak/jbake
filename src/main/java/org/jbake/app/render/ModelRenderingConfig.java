package org.jbake.app.render;

import java.io.File;
import java.util.Map;

public class ModelRenderingConfig extends AbstractRenderingConfig {
	private final Map<String, Object> model;

	public ModelRenderingConfig(Renderer renderer, String fileName, Map<String, Object> model, String templateType) {
		super(new File(renderer.getDestination().getPath() + File.separator + fileName), fileName, renderer.findTemplateName(templateType));
		this.model = model;
	}

	public ModelRenderingConfig(File path, String name, Map<String, Object> model, String template) {
		super(path, name, template);
		this.model = model;
	}

	@Override
	public Map<String, Object> getModel() {
		return model;
	}
}
