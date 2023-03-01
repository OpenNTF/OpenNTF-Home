// TODO figure out why JAX-B doesn't pick up on these
@XmlSchema(
	xmlns = {
		@XmlNs(namespaceURI=Rss.NS_CONTENT, prefix="content"),
		@XmlNs(namespaceURI=Rss.NS_DC, prefix="dc"),
		@XmlNs(namespaceURI=Rss.NS_ATOM, prefix="atom")
	}
)
package api.rss20.model;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;