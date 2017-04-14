package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.render.RendererFactory;
import org.jbake.render.support.MockCompositeConfiguration;
import org.jbake.template.RenderingException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbake.app.ConfigUtil.Keys.PAGINATE_INDEX;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndexRendererTest {
    private IndexRenderer renderer;
    private CompositeConfiguration configuration;
    private ContentStore contentStore;
    private org.jbake.app.render.IndexRenderer mockIndexRenderer;
    private RendererFactory mockFactory;

    @Test
    public void returnsZeroWhenConfigDoesNotRenderIndices() throws RenderingException {
        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderIndices() throws Exception {
        renderer.render(mockFactory, configuration);

        verify(mockIndexRenderer, never()).renderIndex(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersIndices() throws RenderingException {
        setupConfigWithDefaultTrueBoolean();

        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesNotRenderIndices() throws Exception {
        setupNoPaginationConfigWithDefaultTrueBoolean();

        renderer.render(mockFactory, configuration);

        verify(mockIndexRenderer, times(1)).renderIndex("random string");
    }

    @Test(expected = RenderingException.class)
    public void propagatesRenderingException() throws Exception {
        setupNoPaginationConfigWithDefaultTrueBoolean();

        doThrow(new Exception()).when(mockIndexRenderer).renderIndex(anyString());

        renderer.render(mockFactory, configuration);

        verify(mockIndexRenderer, never()).renderIndex("random string");
    }


    /**
     * @see <a href="https://github.com/jbake-org/jbake/issues/332">Issue 332</a>
     */
    @Test
    public void shouldFallbackToStandardIndexRenderingIfPropertyIsMissing() throws Exception {
        setupConfigWithDefaultTrueBoolean();

        renderer.render(mockFactory, configuration);

        verify(mockIndexRenderer, times(1)).renderIndex("random string");
    }

    @Before
    public void setup() {
        renderer = new IndexRenderer();
        mockIndexRenderer = mock(org.jbake.app.render.IndexRenderer.class);
        when(mockIndexRenderer.getDestination()).thenReturn(new File("fake"));

        configuration = new MockCompositeConfiguration().withDefaultBoolean(false);
        when(mockIndexRenderer.getConfig()).thenReturn(configuration);

        contentStore = mock(ContentStore.class);
        when(mockIndexRenderer.getDb()).thenReturn(contentStore);


        mockFactory = mock(RendererFactory.class);
        when(mockFactory.indexRenderer()).thenReturn(mockIndexRenderer);
    }

    private void setupNoPaginationConfigWithDefaultTrueBoolean() {
        configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
        configuration.setProperty(PAGINATE_INDEX, false);
        when(mockIndexRenderer.getConfig()).thenReturn(configuration);
    }

    private void setupConfigWithDefaultTrueBoolean() {
        configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
        when(mockIndexRenderer.getConfig()).thenReturn(configuration);
    }
}