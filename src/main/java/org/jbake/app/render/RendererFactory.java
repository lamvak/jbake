package org.jbake.app.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;

import java.io.File;

public class RendererFactory implements Renderers {
	private final ContentStore db;
	private final File destination;
	private final File templatesPath;
	private final CompositeConfiguration config;

	public RendererFactory(ContentStore db, File destination, File templatesPath,
												 CompositeConfiguration config) {
		this.db = db;
		this.destination = destination;
		this.templatesPath = templatesPath;
		this.config = config;
	}
	@Override
	public ArchiveRenderer archiveRenderer() {
		return new ArchiveRenderer(db, destination, templatesPath, config);
	}

	@Override
	public FeedRenderer feedRenderer() {
		return new FeedRenderer(db, destination, templatesPath, config);
	}

	@Override
	public IndexRenderer indexRenderer() {
		return new IndexRenderer(db, destination, templatesPath, config);
	}

	@Override
	public SiteMapRenderer siteMapRenderer() {
		return new SiteMapRenderer(db, destination, templatesPath, config);
	}

	@Override
	public TagsRenderer tagsRenderer() {
		return new TagsRenderer(db, destination, templatesPath, config);
	}

	@Override
	public Renderer defaultRenderer() {
		return new Renderer(db, destination, templatesPath, config);
	}
}
