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

public class FeedRendererTest {
	private FeedRenderer renderer;
	private CompositeConfiguration configuration;
	private ContentStore contentStore;
	private RendererFactory mockFactory;
	private org.jbake.app.render.FeedRenderer mockFeedRenderer;

	@Test
	public void returnsZeroWhenConfigDoesNotRenderFeeds() throws RenderingException {
		int renderResponse = renderer.render(mockFactory, configuration);

		assertThat(renderResponse).isEqualTo(0);
	}

	@Test
	public void doesNotRenderWhenConfigDoesNotRenderFeeds() throws Exception {
		renderer.render(mockFactory, configuration);

		verify(mockFeedRenderer, never()).renderFeed(anyString());
	}

	@Test
	public void returnsOneWhenConfigRendersFeeds() throws RenderingException {
		configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
		when(mockFeedRenderer.getConfig()).thenReturn(configuration);

		int renderResponse = renderer.render(mockFactory, configuration);

		assertThat(renderResponse).isEqualTo(1);
	}

	@Test
	public void doesRenderWhenConfigDoesNotRenderFeeds() throws Exception {
		configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
		when(mockFeedRenderer.getConfig()).thenReturn(configuration);

		renderer.render(mockFactory, configuration);

		verify(mockFeedRenderer, times(1)).renderFeed("random string");
	}

	@Test(expected = RenderingException.class)
	public void propogatesRenderingException() throws Exception {
		configuration = new MockCompositeConfiguration().withDefaultBoolean(true);
		when(mockFeedRenderer.getConfig()).thenReturn(configuration);

		doThrow(new Exception()).when(mockFeedRenderer).renderFeed(anyString());

		renderer.render(mockFactory, configuration);

		verify(mockFeedRenderer, never()).renderFeed("random string");
	}

	@Before
	public void setup() {
		renderer = new FeedRenderer();
		mockFeedRenderer = mock(org.jbake.app.render.FeedRenderer.class);

		configuration = new MockCompositeConfiguration().withDefaultBoolean(false);
		when(mockFeedRenderer.getConfig()).thenReturn(configuration);

		contentStore = mock(ContentStore.class);
		when(mockFeedRenderer.getDb()).thenReturn(contentStore);

		mockFactory = mock(RendererFactory.class);
		when(mockFactory.feedRenderer()).thenReturn(mockFeedRenderer);
	}
}


