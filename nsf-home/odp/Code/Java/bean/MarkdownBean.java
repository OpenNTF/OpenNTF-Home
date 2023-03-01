package bean;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("markdown")
public class MarkdownBean {
	private Parser markdown = Parser.builder().build();
	private HtmlRenderer markdownHtml = HtmlRenderer.builder()
			.build();

	public String toHtml(final String text) {
		Node parsed = markdown.parse(text);
		return markdownHtml.render(parsed);
	}
}