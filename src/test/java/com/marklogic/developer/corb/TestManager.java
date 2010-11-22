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
				TestHelper.UNIT_TEST_SETUP, false);

		logger.info("Populating test Database");
		// Now we have the DBs set up, change to:
		xcccp = new XCCConnectionProvider(
				TestHelper.getCorbUnitTestConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_POPULATE_DB, false);
	}

	/*
	 * The following three tests (below) should all throw a RuntimeException as
	 * the Modules can't be located. This is correct behaviour for the
	 * configuration arguments passed in
	 */

	/**
	 * Unit test to prove the medline-iso8601.xqy arguments work
	 */
	@Test(expected = RuntimeException.class)
	public void testFirstSampleInvocationWithoutFirstCopyingTheModules() {
		invokeCorbWithArguments(TestHelper.getFirstSampleInvocation());
	}

	@Test(expected = RuntimeException.class)
	public void testSecondSampleInvocationWithoutFirstCopyingTheModules() {
		invokeCorbWithArguments(TestHelper.getSecondSampleInvocation());
	}

	@Test(expected = RuntimeException.class)
	public void testThirdSampleInvocationWithoutFirstCopyingTheModules() {
		invokeCorbWithArguments(TestHelper.getThirdSampleInvocation());
	}

	/*
	 * The following tests have flags to first install the modules
	 */

	@Test
	public void testThirdSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getThirdSampleInvocationWithFlagToCopyModules());
	}

	@Test
	public void testWithAllArgs() {
		invokeCorbWithArguments(TestHelper.getFullCorbArgs());
	}

	@AfterClass
	public static void tearDown() {
		logger.info("Tearing down unit test");
		xcccp = new XCCConnectionProvider(TestHelper.getConnectionUri());
		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN, true);
		logger.info("Sleeping momentarily while MarkLogic restarts...");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Completing Teardown");

		xcccp.buildConnection(xcccp.getContentSource(),
				TestHelper.UNIT_TEST_TEARDOWN, false);
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
