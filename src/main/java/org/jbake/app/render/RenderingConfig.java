package org.jbake.app.render;

import java.io.File;
import java.util.Map;

interface RenderingConfig {

		File getPath();

		String getName();

		String getTemplate();

		Map<String, Object> getModel();
}
