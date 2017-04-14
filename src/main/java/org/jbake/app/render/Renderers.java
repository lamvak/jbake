package org.jbake.app.render;

public interface Renderers {
	Renderer archiveRenderer();
	Renderer feedRenderer();
	Renderer indexRenderer();
	Renderer siteMapRenderer();
	Renderer tagsRenderer();
	Renderer defaultRenderer();
}
