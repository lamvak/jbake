package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.ContentStore;
import org.jbake.app.Crawler.Attributes;
import org.jbake.app.FileUtil;
import org.jbake.template.DelegatingTemplateEngine;
import org.jbake.util.PagingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jbake.app.render.IndexPagination.NON_PAGINATE;
import static org.jbake.app.render.IndexPagination.PAGINATE;

/**
 * Render output to a file.
 *
 * @author Jonathan Bullock <jonbullock@gmail.com>
 */
public class Renderer {
	private final static Logger LOGGER = LoggerFactory.getLogger(Renderer.class);

	// TODO: should all content be made available to all templates via this class??
	private final File destination;
	private final CompositeConfiguration config;
	private final DelegatingTemplateEngine renderingEngine;
	private final ContentStore db;

	private String docType;
	private String outputFilename;

	/**
	 * Creates a new instance of Renderer with supplied references to folders.
	 *
	 * @param db            The database holding the content
	 * @param destination   The destination folder
	 * @param templatesPath The templates folder
	 */
	public Renderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config) {
		this.destination = destination;
		this.config = config;
		this.renderingEngine = new DelegatingTemplateEngine(config, db, destination, templatesPath);
		this.db = db;
	}

	/**
	 * Creates a new instance of Renderer with supplied references to folders and the instance of
	 * DelegatingTemplateEngine to use.
	 *
	 * @param db              The database holding the content
	 * @param destination     The destination folder
	 * @param templatesPath   The templates folder
	 * @param renderingEngine The instance of DelegatingTemplateEngine to use
	 */
	public Renderer(ContentStore db, File destination, File templatesPath, CompositeConfiguration config, DelegatingTemplateEngine renderingEngine) {
		this.destination = destination;
		this.config = config;
		this.renderingEngine = renderingEngine;
		this.db = db;
	}

	String findTemplateName(String docType) {
		String templateKey = "template." + docType + ".file";
		String returned = config.getString(templateKey);
		return returned;
	}

	/**
	 * Render the supplied content to a file.
	 *
	 * @param content The content to renderDocument
	 */
	public void renderFrom(Map<String, Object> content) throws Exception {
		prepareForRendering(content);
		renderFile(content);
	}

	private void prepareForRendering(Map<String, Object> content) {
		prepareRenderingData(content);
		cleanupFilesBeforeRendering();
		addDraftSuffixToOutputFilenameIfInDraftMode(content);
	}

	private void cleanupFilesBeforeRendering() {
		deleteExistingDraftInCaseStatusChanged();
		deletePublishedFileIfExists();
	}

	private void prepareRenderingData(Map<String, Object> content) {
		prepareDocType(content);
		prepareOutputFilename(content);
	}

	private void renderFile(Map<String, Object> content) throws Exception {
		StringBuilder sb = new StringBuilder();
		File outputFile = prepareOutputFile();
		tryRenderOutput(content, sb, outputFile);
	}

	private void tryRenderOutput(Map<String, Object> content, StringBuilder sb, File outputFile) throws Exception {
		try (Writer out = createWriter(outputFile)) {
			reportRenderingStart(sb, outputFile);
			renderingEngine.renderDocument(prepareModelWithEngineAnd(content), findTemplateName(docType), out);
			reportRenderingDone(sb);
		} catch (Exception e) {
			reportAndRaiseFailedRendering(sb, e, sb.toString(), "Failed to render file. Cause: " + e.getMessage());
		}
	}

	private void reportAndRaiseFailedRendering(StringBuilder sb, Exception e, String s, String message) throws Exception {
		sb.append("failed!");
		LOGGER.error(s, e);
		throw new Exception(message, e);
	}

	private void reportRenderingStart(StringBuilder sb, File outputFile) {
		sb.append("Rendering [").append(outputFile).append("]... ");
	}

	private File prepareOutputFile() {
		return new File(outputFilename + FileUtil.findExtension(config, docType));
	}

	private Map<String, Object> prepareModelWithEngineAnd(Map<String, Object> content) {
		Map<String, Object> model = new HashMap<>();
		model.put("content", content);
		model.put("renderer", renderingEngine);
		return model;
	}

	private void reportRenderingDone(StringBuilder sb) {
		sb.append("done!");
		LOGGER.info(sb.toString());
	}

	private void addDraftSuffixToOutputFilenameIfInDraftMode(Map<String, Object> content) {
		if (content.get(Attributes.STATUS).equals(Attributes.Status.DRAFT)) {
			outputFilename = outputFilename + config.getString(Keys.DRAFT_SUFFIX);
		}
	}

	private void prepareOutputFilename(Map<String, Object> content) {
		outputFilename = destination.getPath() + File.separatorChar + content.get(Attributes.URI);
		if (outputFilename.lastIndexOf(".") > outputFilename.lastIndexOf(File.separatorChar)) {
			outputFilename = outputFilename.substring(0, outputFilename.lastIndexOf("."));
		}
	}

	private void prepareDocType(Map<String, Object> content) {
		docType = (String) content.get(Attributes.TYPE);
	}

	private void deletePublishedFileIfExists() {
		deleteFileIfExists(outputFilename + FileUtil.findExtension(config, docType));
	}

	private void deleteExistingDraftInCaseStatusChanged() {
		deleteFileIfExists(outputFilename + config.getString(Keys.DRAFT_SUFFIX) + FileUtil.findExtension(config, docType));
	}

	private void deleteFileIfExists(String pathName) {
		File draftFile = new File(pathName);
		if (draftFile.exists()) {
			draftFile.delete();
		}
	}

	private Writer createWriter(File file) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		return new OutputStreamWriter(new FileOutputStream(file), config.getString(ConfigUtil.Keys.RENDER_ENCODING));
	}

	private void renderFrom(RenderingConfig renderConfig) throws Exception {
		File outputFile = renderConfig.getPath();
		StringBuilder sb = new StringBuilder();
		reportRenderingStart(renderConfig, outputFile, sb);
		tryRenderFromConfig(renderConfig, outputFile, sb);
	}

	private void tryRenderFromConfig(RenderingConfig renderConfig, File outputFile, StringBuilder sb) throws Exception {
		try (Writer out = createWriter(outputFile)) {
			renderingEngine.renderDocument(renderConfig.getModel(), renderConfig.getTemplate(), out);
			reportRenderingDone(sb);
		} catch (Exception e) {
			reportAndRaiseFailedRendering(sb, e, sb.toString(), "Failed to render " + renderConfig.getName());
		}
	}

	private void reportRenderingStart(RenderingConfig renderConfig, File outputFile, StringBuilder sb) {
		sb.append("Rendering ").append(renderConfig.getName()).append(" [").append(outputFile).append("]...");
	}

	/**
	 * Render an index file using the supplied content.
	 *
	 * @param indexFile The name of the output file
	 */
	public void renderIndex(String indexFile) throws Exception {
		renderFrom(defaultRenderingContextFor(indexFile, "masterindex"));
	}

	private DefaultRenderingConfig defaultRenderingContextFor(String indexFile, String allInOneName) {
		return new DefaultRenderingConfig(
				filePath(indexFile),
				allInOneName,
				findTemplateName(allInOneName),
				buildSimpleModel(allInOneName),
				getRenderingEngine(),
				shouldIndexBePaginated());
	}

	private File filePath(String indexFile) {
		return new File(getDestination().getPath() + File.separator + indexFile);
	}

	private IndexPagination shouldIndexBePaginated() {
		return getConfig().containsKey(Keys.PAGINATE_INDEX) && getConfig().getBoolean
				(Keys.PAGINATE_INDEX) ? PAGINATE : NON_PAGINATE;
	}

	public void renderIndexPaging(String indexFile) throws Exception {
		long totalPosts = db.getPublishedCount("post");
		if (totalPosts == 0) {
			renderIndex(indexFile);
		} else {
			int postsPerPage = config.getInt(Keys.POSTS_PER_PAGE, 5);
			PagingHelper pagingHelper = new PagingHelper(totalPosts, postsPerPage);
			Map<String, Object> model = prepareModelForIndexPaging(pagingHelper);
			db.setLimit(postsPerPage);
			tryRenderPagedIndex(indexFile, totalPosts, postsPerPage, pagingHelper, model);
		}
	}

	private void tryRenderPagedIndex(String indexFile, long totalPosts, int postsPerPage, PagingHelper pagingHelper, Map<String, Object> model) throws Exception {
		try {
			renderPagedIndex(indexFile, totalPosts, postsPerPage, pagingHelper, model);
		} catch (Exception e) {
			throw new Exception("Failed to render index. Cause: " + e.getMessage(), e);
		}
	}

	private void renderPagedIndex(String indexFile, long totalPosts, int postsPerPage, PagingHelper pagingHelper, Map<String, Object> model) throws Exception {
		for (int pageStart = 0, page = 1; pageStart < totalPosts; pageStart += postsPerPage, page++) {
			renderPageOfPagedIndex(indexFile, pagingHelper, model, pageStart, page);
		}
		db.resetPagination();
	}

	private void renderPageOfPagedIndex(String indexFile, PagingHelper pagingHelper, Map<String, Object> model, int pageStart, int page) throws Exception {
		refreshPage(indexFile, pagingHelper, model, pageStart, page);
		ModelRenderingConfig renderConfig = renderingModelConfigForMasterIndex(model,
				addPageNumberToFileName(pagingHelper, page, indexFile));
		renderFrom(renderConfig);
	}

	private void refreshPage(String indexFile, PagingHelper pagingHelper, Map<String, Object> model, int pageStart, int page) {
		db.setStart(pageStart);
		refreshModelPageState(pagingHelper, model, page, indexFile);
	}

	private ModelRenderingConfig renderingModelConfigForMasterIndex(Map<String, Object> model, String fileName) {
		return new ModelRenderingConfig(
				filePath(fileName),
				fileName,
				findTemplateName("masterindex"),
				model);
	}

	private String addPageNumberToFileName(PagingHelper pagingHelper, int page, String fileName) {
		fileName = pagingHelper.getCurrentFileName(page, fileName);
		return fileName;
	}

	private void refreshModelPageState(PagingHelper pagingHelper, Map<String, Object> model, int page, String fileName) {
		model.put("currentPageNumber", page);
		String previous = pagingHelper.getPreviousFileName(page, fileName);
		model.put("previousFileName", previous);
		String nextFileName = pagingHelper.getNextFileName(page, fileName);
		model.put("nextFileName", nextFileName);
	}

	private Map<String, Object> prepareModelForIndexPaging(PagingHelper pagingHelper) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("renderer", renderingEngine);
		model.put("content", buildSimpleModel("masterindex"));
		model.put("numberOfPages", pagingHelper.getNumberOfPages());
		return model;
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
		renderFrom(defaultRenderingContextFor(sitemapFile, allInOneName));

	}

	/**
	 * Render an XML feed file using the supplied content.
	 *
	 * @param feedFile The name of the output file
	 */
	public void renderFeed(String feedFile) throws Exception {
		String allInOneName = "feed";
		renderFrom(defaultRenderingContextFor(feedFile, allInOneName));

	}

	/**
	 * Render an archive file using the supplied content.
	 *
	 * @param archiveFile The name of the output file
	 */
	public void renderArchive(String archiveFile) throws Exception {
		String allInOneName = "archive";
		renderFrom(defaultRenderingContextFor(archiveFile, allInOneName));

	}

	/**
	 * Render tag files using the supplied content.
	 *
	 * @param tagPath The output path
	 */
	public int renderTags(String tagPath) throws Exception {
		Set<String> tags = db.getAllTags();
		renderTags(tagPath, tags);
		return tags.size();
	}

	private void renderTags(String tagPath, Set<String> tags) throws Exception {
		final List<Throwable> errors = new LinkedList<>();
		for (String tag : tags) {
			tryRenderTag(tagPath, errors, tag);
		}
		reportAndRethrowErrors(errors);
	}

	private void tryRenderTag(String tagPath, List<Throwable> errors, String tag) {
		try {
			renderTag(tagPath, tag);
		} catch (Exception e) {
			errors.add(e);
		}
	}

	private void renderTag(String tagPath, String tag) throws Exception {
		Map<String, Object> model = prepareModelForTagsRendering(tag);
		renderFrom(modelConfigForRenderingTag(tagPath, tag, model));
	}

	private ModelRenderingConfig modelConfigForRenderingTag(String tagPath, String tag, Map<String, Object> model) {
		return new ModelRenderingConfig(
				tagOutputFile(tagPath, tag),
				Attributes.TAG,
				findTemplateName(Attributes.TAG),
				model);
	}

	private File tagOutputFile(String tagPath, String tag) {
		return new File(destination.getPath() + File.separator + tagPath + File.separator + tag + config.getString(Keys.OUTPUT_EXTENSION));
	}

	private void reportAndRethrowErrors(List<Throwable> errors) throws Exception {
		if (!errors.isEmpty()) {
			StringBuilder sb = new StringBuilder("Failed to render tags. Cause(s):");
			for (Throwable error : errors) {
				sb.append("\n").append(error.getMessage());
			}
			throw new Exception(sb.toString(), errors.get(0));
		}
	}

	private Map<String, Object> prepareModelForTagsRendering(String tag) {
		Map<String, Object> model = new HashMap<>();
		model.put("renderer", renderingEngine);
		model.put(Attributes.TAG, tag);
		model.put("content", buildContentForTagRendering());
		return model;
	}

	private Map<String, Object> buildContentForTagRendering() {
		Map<String, Object> map = buildSimpleModel(Attributes.TAG);
		map.put(Attributes.ROOTPATH, "../");
		return map;
	}

	/**
	 * Builds simple map of values, which are exposed when rendering index/archive/sitemap/feed/tags.
	 */
	Map<String, Object> buildSimpleModel(String type) {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put(Attributes.TYPE, type);
		content.put(Attributes.ROOTPATH, "");
		// add any more keys here that need to have a default value to prevent need to perform null check in templates
		return content;
	}

	public File getDestination() {
		return destination;
	}

	public CompositeConfiguration getConfig() {
		return config;
	}

	public DelegatingTemplateEngine getRenderingEngine() {
		return renderingEngine;
	}
}
