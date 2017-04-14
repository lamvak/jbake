package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.render.RendererFactory;
import org.jbake.template.RenderingException;

public interface RenderingTool {
	int render(RendererFactory renderer, CompositeConfiguration config) throws RenderingException;
}