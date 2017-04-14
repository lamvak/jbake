package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ContentStore;
import org.jbake.template.DelegatingTemplateEngine;

import java.io.File;

import static org.jbake.app.render.IndexPagination.NON_PAGINATE;
import static org.jbake.app.render.IndexPagination.PAGINATE;

public class SiteMapRenderer extends Renderer {
	public SiteMapRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config) {
		super(db, destination, templatesPath, config);
	}

	public SiteMapRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config, DelegatingTemplateEngine renderingEngine) {
		super(db, destination, templatesPath, config, renderingEngine);
	}

	/**
	 * Render an XML sitemap file using the supplied content.
	 *
	 * @see <a href="https://support.google.com/webmasters/answer/156184?hl=en&ref_topic=8476">About
	 * Sitemaps</a>
	 * @see <a href="http://www.sitemaps.org/">Sitemap protocol</a>
	 */
	public void renderSitemap(String sitemapFile) throws Exception {
		String allInOneName = "sitemap";
		render(new DefaultRenderingConfig(
				new File(getDestination().getPath() + File.separator + sitemapFile),
				allInOneName,
				findTemplateName(allInOneName),
				buildSimpleModel(allInOneName),
				getRenderingEngine(),
				getConfig().containsKey(ConfigUtil.Keys.PAGINATE_INDEX) && getConfig().getBoolean
						(ConfigUtil.Keys.PAGINATE_INDEX) ? PAGINATE : NON_PAGINATE));

	}
}
