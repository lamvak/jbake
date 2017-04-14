package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.render.RendererFactory;
import org.jbake.template.RenderingException;


public class TagsRenderer implements RenderingTool {

	@Override
	public int render(RendererFactory factory, CompositeConfiguration config) throws RenderingException {
		org.jbake.app.render.TagsRenderer tagsRenderer = factory.tagsRenderer();
		if (config.getBoolean(Keys.RENDER_TAGS)) {
			try {
				return tagsRenderer.renderTags(config.getString(Keys.TAG_PATH));
			} catch (Exception e) {
				throw new RenderingException(e);
			}
		} else {
			return 0;
		}
	}

}