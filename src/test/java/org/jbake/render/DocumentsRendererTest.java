package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.DocumentList;
import org.jbake.app.render.Renderer;
import org.jbake.app.render.RendererFactory;
import org.jbake.model.DocumentTypes;
import org.jbake.template.RenderingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentsRendererTest {
    private static final String CUSTOM_TYPE = "customType";
    private DocumentsRenderer documentsRenderer;
    private ContentStore db;
    private Renderer renderer;
    private CompositeConfiguration configuration;
    private DocumentList emptyDocumentList;
    private RendererFactory mockFactory;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldReturnZeroIfNothingHasRendered() throws Exception {
        int renderResponse = documentsRenderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void shouldReturnCountOfProcessedDocuments() throws Exception {
        setupCustomTypeWithEmptyDocuments();

        int renderResponse = documentsRenderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(2);
    }

    @Test
    public void shouldThrowAnExceptionWithCollectedErrorMessages() throws Exception {
        setupExpectedException();
        setupCustomType();

        int renderResponse = documentsRenderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(2);
    }

    @Before
    public void setUp() throws Exception {
        documentsRenderer = new DocumentsRenderer();
        renderer = mock(Renderer.class);
        when(renderer.getDestination()).thenReturn(new File("fakefile"));

        configuration = mock(CompositeConfiguration.class);
        when(renderer.getConfig()).thenReturn(configuration);

        db = mock(ContentStore.class);
        when(renderer.getDb()).thenReturn(db);

        emptyDocumentList = new DocumentList();
        when(db.getUnrenderedContent(anyString())).thenReturn(emptyDocumentList);

        mockFactory = mock(RendererFactory.class);
        when(mockFactory.defaultRenderer()).thenReturn(renderer);
    }

    private void setupExpectedException() throws Exception {
        String fakeExceptionMessage = "fake exception";
        exception.expect(RenderingException.class);
        exception.expectMessage(fakeExceptionMessage+"\n"+fakeExceptionMessage);
        doThrow(new Exception(fakeExceptionMessage)).when(renderer).render(anyMap());
    }

    private void setupCustomType() {
        addCustomType();
        setupDocumentsForCustomType();
    }

    private void setupDocumentsForCustomType() {
        DocumentList documentList = new DocumentList();
        HashMap<String, Object> document = emptyDocument();
        HashMap<String, Object> document2 = emptyDocument();
        documentList.add(document);
        documentList.add(document2);
        when(db.getUnrenderedContent(CUSTOM_TYPE)).thenReturn(documentList);
    }

    private HashMap<String, Object> emptyDocument() {
        return new HashMap<>();
    }

    private void setupCustomTypeWithEmptyDocuments() {
        addCustomType();
        DocumentList documentList = new DocumentList();
        documentList.add(emptyDocument());
        documentList.add(emptyDocument());
        when(db.getUnrenderedContent(CUSTOM_TYPE)).thenReturn(documentList);
    }

    private void addCustomType() {
        DocumentTypes.addDocumentType(CUSTOM_TYPE);
    }
}