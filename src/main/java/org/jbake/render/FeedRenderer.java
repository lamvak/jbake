package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.render.RendererFactory;
import org.jbake.template.RenderingException;


public class FeedRenderer implements RenderingTool {

	@Override
	public int render(RendererFactory factory, CompositeConfiguration config) throws RenderingException {
		org.jbake.app.render.FeedRenderer feedRenderer = factory.feedRenderer();
		if (config.getBoolean(Keys.RENDER_FEED)) {
			try {
				feedRenderer.renderFeed(config.getString(Keys.FEED_FILE));
				return 1;
			} catch (Exception e) {
				throw new RenderingException(e);
			}
		} else {
			return 0;
		}
	}

}