package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.DocumentList;
import org.jbake.app.render.Renderer;
import org.jbake.app.render.RendererFactory;
import org.jbake.model.DocumentTypes;
import org.jbake.template.RenderingException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DocumentsRenderer implements RenderingTool {

    @Override
    public int render(RendererFactory factory, CompositeConfiguration config) throws RenderingException {
        Renderer renderer = factory.defaultRenderer();
        int renderedCount = 0;
        final List<String> errors = new LinkedList<>();
        for (String docType : DocumentTypes.getDocumentTypes()) {
            DocumentList documentList = renderer.getDb().getUnrenderedContent(docType);
            for (Map<String, Object> page : documentList) {
                try {
                    renderer.render(page);
                    renderedCount++;
                } catch (Exception e) {
                    errors.add(e.getMessage());
                }
            }
        }
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to render documents. Cause(s):");
            for (String error : errors) {
                sb.append("\n").append(error);
            }
            throw new RenderingException(sb.toString());
        } else {
            return renderedCount;
        }
    }
}