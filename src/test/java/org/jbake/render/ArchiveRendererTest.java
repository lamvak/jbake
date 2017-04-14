package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.render.RendererFactory;
import org.jbake.render.support.MockCompositeConfiguration;
import org.jbake.template.RenderingException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArchiveRendererTest {
    private ArchiveRenderer renderer;
    private CompositeConfiguration configuration;
    private ContentStore contentStore;
    private org.jbake.app.render.ArchiveRenderer mockArchiveRenderer;
    private RendererFactory mockFactory;

    @Test
    public void returnsZeroWhenConfigDoesNotRenderArchives() throws RenderingException {
        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderArchives() throws Exception {
        renderer.render(mockFactory, configuration);

        verify(mockArchiveRenderer, never()).renderArchive(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersArchives() throws RenderingException {
        setupConfigurationWithTrueDefaultBoolean();

        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesNotRenderArchives() throws Exception {
        setupConfigurationWithTrueDefaultBoolean();

        renderer.render(mockFactory, configuration);

        verify(mockArchiveRenderer, times(1)).renderArchive("random string");
    }

    @Test(expected = RenderingException.class)
    public void propogatesRenderingException() throws Exception {
        setupConfigurationWithTrueDefaultBoolean();
        doThrow(new Exception()).when(mockArchiveRenderer).renderArchive(anyString());

        renderer.render(mockFactory, configuration);

        verify(mockArchiveRenderer, never()).renderArchive("random string");
    }

    @Before
    public void setup() {
        renderer = new ArchiveRenderer();
        mockArchiveRenderer = mock(org.jbake.app.render.ArchiveRenderer.class);

        configuration = new MockCompositeConfiguration().withDefaultBoolean(false);
        when(mockArchiveRenderer.getConfig()).thenReturn(configuration);

        contentStore = mock(ContentStore.class);
        when(mockArchiveRenderer.getDb()).thenReturn(contentStore);

        mockFactory = mock(RendererFactory.class);
        when(mockFactory.archiveRenderer()).thenReturn(mockArchiveRenderer);
    }

    private void setupConfigurationWithTrueDefaultBoolean() {
        configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
        when(mockArchiveRenderer.getConfig()).thenReturn(configuration);
    }
}


