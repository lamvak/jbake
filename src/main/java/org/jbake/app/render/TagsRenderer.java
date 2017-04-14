package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ContentStore;
import org.jbake.app.Crawler;
import org.jbake.template.DelegatingTemplateEngine;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TagsRenderer extends Renderer {
	public TagsRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config) {
		super(db, destination, templatesPath, config);
	}

	public TagsRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config, DelegatingTemplateEngine renderingEngine) {
		super(db, destination, templatesPath, config, renderingEngine);
	}

	/**
	 * Render tag files using the supplied content.
	 *
	 * @param tagPath The output path
	 */
	public int renderTags(String tagPath) throws Exception {
		int renderedCount = 0;
		final List<Throwable> errors = new LinkedList<>();
		for (String tag : getDb().getAllTags()) {
			try {
				Map<String, Object> model = new HashMap<>();
				model.put("renderer", getRenderingEngine());
				model.put(Crawler.Attributes.TAG, tag);
				Map<String, Object> map = buildSimpleModel(Crawler.Attributes.TAG);
				map.put(Crawler.Attributes.ROOTPATH, "../");
				model.put("content", map);

				File path = new File(getDestination().getPath() + File.separator + tagPath + File.separator + tag + getConfig().getString(ConfigUtil.Keys.OUTPUT_EXTENSION));
				render(new ModelRenderingConfig(
						path,
						Crawler.Attributes.TAG,
						findTemplateName(Crawler.Attributes.TAG),
						model));
				renderedCount++;
			} catch (Exception e) {
				errors.add(e);
			}
		}
		if (!errors.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Failed to render tags. Cause(s):");
			for (Throwable error : errors) {
				sb.append("\n").append(error.getMessage());
			}
			throw new Exception(sb.toString(), errors.get(0));
		} else {
			return renderedCount;
		}
	}

}
