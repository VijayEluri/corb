package com.marklogic.developer.corb;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.marklogic.developer.SimpleLogger;
import com.marklogic.developer.TestHelper;
import com.marklogic.developer.XCCConnectionProvider;

@RunWith(Suite.class)
@SuiteClasses({ TestManager.class })
public class TestSuite {

	static SimpleLogger logger;
	static XCCConnectionProvider xcccp;

	@BeforeClass
	public static void setUp() {
		logger = SimpleLogger.getSimpleLogger();
		logger.info("Setting up unit test");
		logger.info("Creating Databases and Forests for tests");
		xcccp = new XCCConnectionProvider(TestHelper.getConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_SETUP, false);

		logger.info("Populating test Database");
		// Now we have the DBs set up, change to:
		xcccp = new XCCConnectionProvider(
				TestHelper.getCorbUnitTestConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_POPULATE_DB, false);
	}

	@AfterClass
	public static void tearDown() {
		logger.info("Tearing down unit test");
		xcccp = new XCCConnectionProvider(TestHelper.getConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN, true);
		logger.info("Sleeping momentarily while MarkLogic restarts...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Completing Teardown");

		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN, false);
	}

}
