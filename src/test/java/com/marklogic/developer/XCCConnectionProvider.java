package com.marklogic.developer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
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

	public void buildConnection(String queryFilePath) {
		try {
			logger.fine("Creating a new Session");
			Session session = getContentSource().newSession();
			logger.fine("Creating an AdHoc Query");

			Request request = session.newAdhocQuery(Utilities
					.readFile(queryFilePath));

			// TODO - allow an array of bindings to be passed in (at some stage)
			// logger.fine("Configuring external Variable bindings");
			// request = TestHelper.setTestConfigurationVariables(request,
			// startTeardown);
			logger.fine("Submitting request..");
			ResultSequence rs = session.submitRequest(request);
			logger.fine(rs.asString());
			session.close();
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (RequestException e) {
			logger.severe(e.getMessage());
		}
	}

	public String getEstimatedDocsInDb() {
		String result = null;
		try {
			Session session = getContentSource().newSession();
			Request request = session.newAdhocQuery("xdmp:estimate(doc())");
			ResultSequence rs = session.submitRequest(request);
			result = rs.asString();
			session.close();
		} catch (RequestException e) {
			logger.severe(e.getMessage());
		}
		return result;
	}
}