package com.marklogic.tools;

import java.io.IOException;

import com.marklogic.developer.TestHelper;
import com.marklogic.developer.Utilities;
import com.marklogic.developer.XCCConnectionProvider;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * This can be used if something happens and the teardown doesn't seem to
 * complete as we would have hoped... It shouldn't be required if things are
 * running smoothly.
 * 
 * @author ableasdale
 * 
 */

public class TeardownOnTestSuiteFailure {

	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!! THIS CLASS SHOULD BE DEPRECATED AND SHOULD ONLY BE USED IF
	 * ABSOLUTELY NECESSARY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * The TestSuite should manage both setup and teardown methods. Always
	 * invoke the unit test by running the TestSuite
	 * 
	 * Hit the main method if it looks like you're having problems cleaning up
	 * after running the unit tests.
	 * 
	 * @param args
	 */

	static XCCConnectionProvider xcccp;

	public static void main(String[] args) {
		xcccp = new XCCConnectionProvider(TestHelper.getConnectionUri());
		ContentSource cs = xcccp.getContentSource();
		buildConnection(cs, TestHelper.UNIT_TEST_TEARDOWN, true);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		buildConnection(cs, TestHelper.UNIT_TEST_TEARDOWN, false);
	}

	/**
	 * Builds the connection for the setup and teardown.
	 * 
	 * @param cs
	 *            the ContentSource
	 * @param queryFilePath
	 *            the query file path
	 * @param startTeardown
	 *            the start teardown boolean flag
	 */
	public static void buildConnection(ContentSource cs, String queryFilePath,
			Boolean startTeardown) {
		try {

			Session session = xcccp.getContentSource().newSession();

			Request request = session.newAdhocQuery(Utilities
					.readFile(queryFilePath));
			request = TestHelper.setTestConfigurationVariables(request,
					startTeardown);
			ResultSequence rs = session.submitRequest(request);
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
}
