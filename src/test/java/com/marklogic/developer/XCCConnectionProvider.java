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

public class XCCConnectionProvider {

	private URI uri;
	private final SimpleLogger logger;
	private ContentSource contentSource;

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

	public ContentSource getContentSource() {
		return contentSource;
	}

	public void buildConnection(ContentSource cs, String queryFilePath,
			Boolean startTeardown) {
		try {
			logger.fine("Creating a new Session");
			Session session = getContentSource().newSession();
			logger.fine("Creating an AdHoc Query");

			Request request = session.newAdhocQuery(Utilities
					.readFile(queryFilePath));
			logger.fine("Configuring external Variable bindings");
			request = TestHelper.setTestConfigurationVariables(request,
					startTeardown);
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

}