package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ContentStore;
import org.jbake.template.DelegatingTemplateEngine;

import java.io.File;

import static org.jbake.app.render.IndexPagination.NON_PAGINATE;
import static org.jbake.app.render.IndexPagination.PAGINATE;

public class ArchiveRenderer extends Renderer {
	public ArchiveRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config) {
		super(db, destination, templatesPath, config);
	}

	public ArchiveRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config, DelegatingTemplateEngine renderingEngine) {
		super(db, destination, templatesPath, config, renderingEngine);
	}

	/**
	 * Render an archive file using the supplied content.
	 *
	 * @param archiveFile The name of the output file
	 */
	public void renderArchive(String archiveFile) throws Exception {
		String allInOneName = "archive";
		render(new DefaultRenderingConfig(
				new File(getDestination().getPath() + File.separator + archiveFile),
				allInOneName,
				findTemplateName(allInOneName),
				buildSimpleModel(allInOneName),
				getRenderingEngine(),
				getConfig().containsKey(ConfigUtil.Keys.PAGINATE_INDEX) && getConfig().getBoolean
						(ConfigUtil.Keys.PAGINATE_INDEX) ? PAGINATE : NON_PAGINATE));

	}
}
