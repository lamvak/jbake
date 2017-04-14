package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.render.RendererFactory;
import org.jbake.app.render.SiteMapRenderer;
import org.jbake.template.RenderingException;


public class SitemapRenderer implements RenderingTool {

	@Override
	public int render(RendererFactory factory, CompositeConfiguration config) throws RenderingException {
		SiteMapRenderer siteMapRenderer = factory.siteMapRenderer();
		if (config.getBoolean(Keys.RENDER_SITEMAP)) {
			try {
				siteMapRenderer.renderSitemap(config.getString(Keys.SITEMAP_FILE));
				return 1;
			} catch (Exception e) {
				throw new RenderingException(e);
			}
		} else {
			return 0;
		}
	}

}