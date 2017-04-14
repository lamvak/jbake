package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ConfigUtil.Keys;
import org.jbake.app.render.RendererFactory;
import org.jbake.template.RenderingException;

public class IndexRenderer implements RenderingTool {
    @Override
    public int render(RendererFactory factory, CompositeConfiguration config) throws RenderingException {
        org.jbake.app.render.IndexRenderer indexRenderer = factory.indexRenderer();
        if (config.getBoolean(Keys.RENDER_INDEX)) {
            try {
                if (shouldPaginateIndex(config)) {
                    indexRenderer.renderIndexPaging(config.getString(Keys.INDEX_FILE));
                } else {
                    indexRenderer.renderIndex(config.getString(Keys.INDEX_FILE));
                }
                return 1;
            } catch (Exception e) {
                throw new RenderingException(e);
            }
        } else {
            return 0;
        }
    }

    private boolean shouldPaginateIndex(CompositeConfiguration config) {
        return config.containsKey(Keys.PAGINATE_INDEX) && config.getBoolean(Keys.PAGINATE_INDEX);
    }

}