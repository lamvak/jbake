package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.ContentStore;
import org.jbake.app.Crawler;
import org.jbake.app.Crawler.Attributes;
import org.jbake.app.FileUtil;
import org.jbake.template.DelegatingTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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
	public void render(Map<String, Object> content) throws Exception {
		String docType = (String) content.get(Crawler.Attributes.TYPE);
		String outputFilename = destination.getPath() + File.separatorChar + content.get(Attributes.URI);
		if (outputFilename.lastIndexOf(".") > outputFilename.lastIndexOf(File.separatorChar)) {
			outputFilename = outputFilename.substring(0, outputFilename.lastIndexOf("."));
		}

		// delete existing versions if they exist in case status has changed either way
		File draftFile = new File(outputFilename + config.getString(Keys.DRAFT_SUFFIX) + FileUtil.findExtension(config, docType));
		if (draftFile.exists()) {
			draftFile.delete();
		}

		File publishedFile = new File(outputFilename + FileUtil.findExtension(config, docType));
		if (publishedFile.exists()) {
			publishedFile.delete();
		}

		if (content.get(Crawler.Attributes.STATUS).equals(Crawler.Attributes.Status.DRAFT)) {
			outputFilename = outputFilename + config.getString(Keys.DRAFT_SUFFIX);
		}

		File outputFile = new File(outputFilename + FileUtil.findExtension(config, docType));
		StringBuilder sb = new StringBuilder();
		sb.append("Rendering [").append(outputFile).append("]... ");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("content", content);
		model.put("renderer", renderingEngine);

		try {
			Writer out = createWriter(outputFile);
			renderingEngine.renderDocument(model, findTemplateName(docType), out);
			out.close();
			sb.append("done!");
			LOGGER.info(sb.toString());
		} catch (Exception e) {
			sb.append("failed!");
			LOGGER.error(sb.toString(), e);
			throw new Exception("Failed to render file. Cause: " + e.getMessage(), e);
		}
	}

	private Writer createWriter(File file) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		return new OutputStreamWriter(new FileOutputStream(file), config.getString(ConfigUtil.Keys.RENDER_ENCODING));
	}

	void render(RenderingConfig renderConfig) throws Exception {
		File outputFile = renderConfig.getPath();
		StringBuilder sb = new StringBuilder();
		sb.append("Rendering ").append(renderConfig.getName()).append(" [").append(outputFile).append("]...");

		try {
			Writer out = createWriter(outputFile);
			renderingEngine.renderDocument(renderConfig.getModel(), renderConfig.getTemplate(), out);
			out.close();
			sb.append("done!");
			LOGGER.info(sb.toString());
		} catch (Exception e) {
			sb.append("failed!");
			LOGGER.error(sb.toString(), e);
			throw new Exception("Failed to render " + renderConfig.getName(), e);
		}
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

	public ContentStore getDb() {
		return db;
	}
}