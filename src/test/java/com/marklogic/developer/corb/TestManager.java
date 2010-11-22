package com.marklogic.developer.corb;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.developer.SimpleLogger;
import com.marklogic.developer.TestHelper;
import com.marklogic.developer.XCCConnectionProvider;

/**
 * com.marklogic.developer.corb.Manager xcc://user:password@host:port/[ database
 * ] input-selector module-name.xqy [ thread-count [ uris-module [ module-root [
 * modules-database [ install ] ] ] ] ]
 * 
 * @author ableasdale
 * 
 */
public class TestManager {

	// String home;
	// String corbModuleFolder;
	static SimpleLogger logger;
	static XCCConnectionProvider xcccp;

	// URI connectionUri = null;

	@BeforeClass
	public static void setup() {
		logger = SimpleLogger.getSimpleLogger();
		logger.info("Setting up unit test");
		logger.info("Creating Databases and Forests for tests");
		xcccp = new XCCConnectionProvider(TestHelper.getConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_SETUP);

		logger.info("Populating test Database");
		// Now we have the DBs set up, change to:
		xcccp = new XCCConnectionProvider(
				TestHelper.getCorbUnitTestConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_POPULATE_DB);
	}

	/**
	 * Unit test to prove the medline-iso8601.xqy arguments work
	 */
	@Test
	public void testFirstSampleInvocation() {
		invokeCorbWithArguments(TestHelper.getFirstSampleInvocation());
	}

	@Test
	public void testSecondSampleInvocation() {
		invokeCorbWithArguments(TestHelper.getSecondSampleInvocation());
	}

	@Test
	public void testThirdSampleInvocation() {
		invokeCorbWithArguments(TestHelper.getThirdSampleInvocation());
	}

	@AfterClass
	public static void tearDown() {
		logger.info("Tearing down unit test");
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN);
	}

	private void invokeCorbWithArguments(String[] arguments) {
		logger.info(MessageFormat.format("Starting CORB on: {0}", new Date()));

		try {
			Manager.main(arguments);
		} catch (URISyntaxException e) {
			logger.severe(e.getMessage());
		}
	}

}
