package com.marklogic.developer.corb;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.marklogic.developer.SimpleLogger;
import com.marklogic.developer.TestHelper;
import com.marklogic.developer.Utilities;
import com.marklogic.developer.XCCConnectionProvider;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

@RunWith(Suite.class)
@SuiteClasses({ ManagerTest.class })
public class TestSuite {

	static SimpleLogger logger;
	static XCCConnectionProvider xcccp;

	@BeforeClass
	public static void setUp() {
		logger = SimpleLogger.getSimpleLogger();
		logger.info("Setting up unit test");
		logger.info("Creating Databases and Forests for tests");
		xcccp = new XCCConnectionProvider(TestHelper.getAdminConnectionUri());
		buildConnection(xcccp.getContentSource(), TestHelper.UNIT_TEST_SETUP,
				false);

		logger.info("Populating test Database");
		// Now we have the DBs set up, change to:
		xcccp = new XCCConnectionProvider(
				TestHelper.getAdminConnectionUriWithDatabaseUri());
		buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_POPULATE_DB, false);
	}

	@AfterClass
	public static void tearDown() {
		logger.info("Tearing down unit test");
		xcccp = new XCCConnectionProvider(TestHelper.getAdminConnectionUri());
		buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN, true);
		logger.info("Sleeping momentarily while MarkLogic restarts...");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			logger.severe(e.getMessage());
		}
		logger.info("Completing Teardown (May take some time while ML restarts and reconfigures)");

		buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN, false);
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
			logger.fine("Creating a new Session");
			Session session = xcccp.getContentSource().newSession();
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
