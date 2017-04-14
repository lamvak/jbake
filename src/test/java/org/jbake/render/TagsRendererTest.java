package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.render.RendererFactory;
import org.jbake.render.support.MockCompositeConfiguration;
import org.jbake.template.RenderingException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TagsRendererTest {
    private TagsRenderer renderer;
    private CompositeConfiguration configuration;
    private ContentStore contentStore;
    private org.jbake.app.render.TagsRenderer mockTagsRenderer;
    private RendererFactory mockFactory;

    @Test
    public void returnsZeroWhenConfigDoesNotRenderTags() throws RenderingException {
        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderTags() throws Exception {
        renderer.render(mockFactory, configuration);

        verify(mockTagsRenderer, never()).renderTags(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersIndices() throws Exception {
        setupConfigWithDefaultTrueBoolean();
        when(mockTagsRenderer.renderTags("random string")).thenReturn(1);
        setupTags();

        int renderResponse = renderer.render(mockFactory, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesNotRenderIndices() throws Exception {
        setupConfigWithDefaultTrueBoolean();
        setupTags();

        renderer.render(mockFactory, configuration);

        verify(mockTagsRenderer, times(1)).renderTags("random string");
    }

    @Test(expected = RenderingException.class)
    public void propogatesRenderingException() throws Exception {
        setupConfigWithDefaultTrueBoolean();

        doThrow(new Exception()).when(mockTagsRenderer).renderTags(anyString());

        renderer.render(mockFactory, configuration);

        verify(mockTagsRenderer, never()).renderTags(anyString());
    }

    @Before
    public void setup() {
        renderer = new TagsRenderer();

        configuration = new MockCompositeConfiguration().withDefaultBoolean(false);
        contentStore = mock(ContentStore.class);

        mockTagsRenderer = mock(org.jbake.app.render.TagsRenderer
            .class);
        when(mockTagsRenderer.getConfig()).thenReturn(configuration);
        when(mockTagsRenderer.getDb()).thenReturn(contentStore);
        when(mockTagsRenderer.getDestination()).thenReturn(new File("fake"));

        mockFactory = mock(RendererFactory.class);
        when(mockFactory.tagsRenderer()).thenReturn(mockTagsRenderer);
    }


    private void setupTags(){
        Set<String> tags = new HashSet<>(Arrays.asList("tag1", "tags2"));
        when(contentStore.getTags()).thenReturn(tags);
    }

    private void setupConfigWithDefaultTrueBoolean(){
        configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
        when(mockTagsRenderer.getConfig()).thenReturn(configuration);
    }
}


