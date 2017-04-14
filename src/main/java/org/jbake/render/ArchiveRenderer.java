package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.render.RendererFactory;
import org.jbake.template.RenderingException;


public class ArchiveRenderer implements RenderingTool {

	@Override
	public int render(RendererFactory factory, CompositeConfiguration config) throws RenderingException {

		org.jbake.app.render.ArchiveRenderer archiveRenderer = factory.archiveRenderer();

		if (config.getBoolean(Keys.RENDER_ARCHIVE)) {
			try {
				archiveRenderer.renderArchive(config.getString(Keys.ARCHIVE_FILE));
				return 1;
			} catch (Exception e) {
				throw new RenderingException(e);
			}
		} else {
			return 0;
		}
	}

}