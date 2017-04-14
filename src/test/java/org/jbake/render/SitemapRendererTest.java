package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.render.RendererFactory;
import org.jbake.app.render.SiteMapRenderer;
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

public class SitemapRendererTest {
    private ContentStore contentStore;
    private CompositeConfiguration configuration;
    private SitemapRenderer renderer;
    private SiteMapRenderer mockSiteMapRenderer;
    private RendererFactory mockFactory;

    @Before
    public void setup() {
        renderer = new SitemapRenderer();
        mockSiteMapRenderer = mock(SiteMapRenderer.class);

        configuration = new MockCompositeConfiguration().withDefaultBoolean(false);
        when(mockSiteMapRenderer.getConfig()).thenReturn(configuration);

        contentStore = mock(ContentStore.class);
        when(mockSiteMapRenderer.getDb()).thenReturn(contentStore);

        mockFactory = mock(RendererFactory.class);
        when(mockFactory.siteMapRenderer()).thenReturn(mockSiteMapRenderer);
    }

    @Test
    public void returnsZeroWhenConfigDoesNotRenderSitemaps() throws RenderingException {
        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderSitemaps() throws Exception {
        renderer.render(mockFactory, configuration);

        verify(mockSiteMapRenderer, never()).renderSitemap(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersSitemaps() throws RenderingException {
        setupConfigurationWithDefaultFalseBoolean();

        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesNotRenderSitemaps() throws Exception {
        setupConfigurationWithDefaultFalseBoolean();
        renderer.render(mockFactory, configuration);

        verify(mockSiteMapRenderer, times(1)).renderSitemap("random string");
    }

    @Test(expected = RenderingException.class)
    public void propogatesRenderingException() throws Exception {
        setupConfigurationWithDefaultFalseBoolean();
        doThrow(new Exception()).when(mockSiteMapRenderer).renderSitemap(anyString());

        renderer.render(mockFactory, configuration);

        verify(mockSiteMapRenderer, never()).renderSitemap("random string");
    }

    private void setupConfigurationWithDefaultFalseBoolean() {
        configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
        when(mockSiteMapRenderer.getConfig()).thenReturn(configuration);
    }
}


