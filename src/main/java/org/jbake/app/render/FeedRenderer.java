package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ContentStore;
import org.jbake.template.DelegatingTemplateEngine;

import java.io.File;

import static org.jbake.app.render.IndexPagination.NON_PAGINATE;
import static org.jbake.app.render.IndexPagination.PAGINATE;

public class FeedRenderer extends Renderer {
	public FeedRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config) {
		super(db, destination, templatesPath, config);
	}

	public FeedRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config, DelegatingTemplateEngine renderingEngine) {
		super(db, destination, templatesPath, config, renderingEngine);
	}

	/**
	 * Render an XML feed file using the supplied content.
	 *
	 * @param feedFile The name of the output file
	 */
	public void renderFeed(String feedFile) throws Exception {
		String allInOneName = "feed";
		render(new DefaultRenderingConfig(
				new File(getDestination().getPath() + File.separator + feedFile),
				allInOneName,
				findTemplateName(allInOneName),
				buildSimpleModel(allInOneName),
				getRenderingEngine(),
				getConfig().containsKey(ConfigUtil.Keys.PAGINATE_INDEX) && getConfig().getBoolean
						(ConfigUtil.Keys.PAGINATE_INDEX) ? PAGINATE : NON_PAGINATE));

	}
}
