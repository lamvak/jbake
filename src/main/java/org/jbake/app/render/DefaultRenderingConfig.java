package org.jbake.app.render;

import org.jbake.template.DelegatingTemplateEngine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class DefaultRenderingConfig extends AbstractRenderingConfig {

	private final Object content;
	private DelegatingTemplateEngine engine;
	private IndexPagination indexPagination;

	public DefaultRenderingConfig(File path, String name, String template, Object content,
																DelegatingTemplateEngine engine, IndexPagination indexPagination) {
		super(path, name, template);
		this.content = content;
		this.engine = engine;
		this.indexPagination = indexPagination;
	}

	@Override
	public Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<>();
		fillInModelValues(model);
		return model;
	}

	private void fillInModelValues(Map<String, Object> model) {
		model.put("renderer", engine);
		model.put("content", content);
		applyAsRequiredIndexPaginationTo(model);
	}

	private void applyAsRequiredIndexPaginationTo(Map<String, Object> model) {
		if (indexPagination.shouldBeApplied()) {
			model.put("numberOfPages", 0);
			model.put("currentPageNumber", 0);
			model.put("previousFileName", "");
			model.put("nextFileName", "");
		}
	}

}
