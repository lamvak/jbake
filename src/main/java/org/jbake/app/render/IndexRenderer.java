package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ContentStore;
import org.jbake.template.DelegatingTemplateEngine;
import org.jbake.util.PagingHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.jbake.app.render.IndexPagination.NON_PAGINATE;
import static org.jbake.app.render.IndexPagination.PAGINATE;

public class IndexRenderer extends Renderer {
	public IndexRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config) {
		super(db, destination, templatesPath, config);
	}

	public IndexRenderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config, DelegatingTemplateEngine renderingEngine) {
		super(db, destination, templatesPath, config, renderingEngine);
	}

	/**
	 * Render an index file using the supplied content.
	 *
	 * @param indexFile The name of the output file
	 */
	public void renderIndex(String indexFile) throws Exception {
		String allInOneName = "masterindex";
		render(new DefaultRenderingConfig(
				new File(getDestination().getPath() + File.separator + indexFile),
				allInOneName,
				findTemplateName(allInOneName),
				buildSimpleModel(allInOneName),
				getRenderingEngine(),
				getConfig().containsKey(ConfigUtil.Keys.PAGINATE_INDEX) && getConfig().getBoolean
						(ConfigUtil.Keys.PAGINATE_INDEX) ? PAGINATE : NON_PAGINATE));
	}

	public void renderIndexPaging(String indexFile) throws Exception {
		long totalPosts = getDb().getPublishedCount("post");
		int postsPerPage = getConfig().getInt(ConfigUtil.Keys.POSTS_PER_PAGE, 5);

		if (totalPosts == 0) {
			//paging makes no sense. render single index file instead
			renderIndex(indexFile);
		} else {

			PagingHelper pagingHelper = new PagingHelper(totalPosts, postsPerPage);

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("renderer", getRenderingEngine());
			model.put("content", buildSimpleModel("masterindex"));
			model.put("numberOfPages", pagingHelper.getNumberOfPages());

			getDb().setLimit(postsPerPage);

			try {

				for (int pageStart = 0, page = 1; pageStart < totalPosts; pageStart += postsPerPage, page++) {
					String fileName = indexFile;

					getDb().setStart(pageStart);
					model.put("currentPageNumber", page);
					String previous = pagingHelper.getPreviousFileName(page, fileName);
					model.put("previousFileName", previous);
					String nextFileName = pagingHelper.getNextFileName(page, fileName);
					model.put("nextFileName", nextFileName);

					// Add page number to file name
					fileName = pagingHelper.getCurrentFileName(page, fileName);
					ModelRenderingConfig renderConfig = new ModelRenderingConfig(
							new File(getDestination().getPath() + File.separator + fileName),
							fileName,
							findTemplateName("masterindex"),
							model);
					render(renderConfig);
				}
				getDb().resetPagination();
			} catch (Exception e) {
				throw new Exception("Failed to render index. Cause: " + e.getMessage(), e);
			}
		}
	}

}
