package org.jbake.app.render;

import org.jbake.app.ConfigUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class DefaultRenderingConfig extends AbstractRenderingConfig {
	private Renderer renderer;
	private final Object content;

	public DefaultRenderingConfig(Renderer renderer, String filename, String allInOneName) {
		super(new File(renderer.getDestination().getPath() + File.separator + filename), allInOneName, renderer
				.findTemplateName(allInOneName));
		this.renderer = renderer;
		this.content = renderer.buildSimpleModel(allInOneName);
	}

	@Override
	public Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("renderer", renderer.getRenderingEngine());
		model.put("content", content);

		if (renderer.getConfig().containsKey(ConfigUtil.Keys.PAGINATE_INDEX) && renderer.getConfig().getBoolean(ConfigUtil.Keys.PAGINATE_INDEX)) {
			model.put("numberOfPages", 0);
			model.put("currentPageNumber", 0);
			model.put("previousFileName", "");
			model.put("nextFileName", "");
		}

		return model;
	}

}
