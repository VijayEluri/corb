package com.marklogic.developer;

import java.net.URI;
import java.net.URISyntaxException;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.exceptions.XccConfigException;

/**
 * The Class XCCConnectionProvider.
 * 
 * @author ableasdale
 */
public class XCCConnectionProvider {

	/** The uri. */
	private URI uri;

	/** The logger. */
	private final SimpleLogger logger;

	/** The content source. */
	private ContentSource contentSource;

	/**
	 * Instantiates a new XCC/J connection provider.
	 * 
	 * @param uriString
	 *            the URI as a String
	 */
	public XCCConnectionProvider(String uriString) {
		logger = SimpleLogger.getSimpleLogger();
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e) {
			logger.severe(e.getMessage());
		}

		try {
			contentSource = ContentSourceFactory.newContentSource(uri);
		} catch (XccConfigException e) {
			logger.severe(e.getMessage());
		}

	}

	/**
	 * Gets the content source.
	 * 
	 * @return the content source
	 */
	public ContentSource getContentSource() {
		return contentSource;
	}

}