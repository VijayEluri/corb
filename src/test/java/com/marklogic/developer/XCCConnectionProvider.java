package com.marklogic.developer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

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

	// TODO - constructor / singleton

	public XCCConnectionProvider(String uriString) {
		logger = SimpleLogger.getSimpleLogger();
		// logger.info("XCCConnectionProvider :: INIT - todo - fix so this is a singleton on demand");
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

	public void buildConnection(ContentSource cs, String queryFilePath) {
		try {
			logger.fine("Creating a new Session");
			Session session = getContentSource().newSession();
			logger.fine("Creating an AdHoc Query");
			Request request = session.newAdhocQuery(readFile(queryFilePath));
			logger.fine("Configuring external Variable bindings");
			request = TestHelper.setTestConfigurationVariables(request);
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

	/**
	 * Takes a file path (as represented by a String) and returns a UTF-8
	 * decoded String containing the content of the file. Useful for pulling
	 * text content into AdHoc queries.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			return Charset.forName("UTF-8").decode(bb).toString();
		} finally {
			stream.close();
		}
	}
}