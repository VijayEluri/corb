package com.marklogic.developer.corb;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

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
public class ManagerTest {

	// String home;
	// String corbModuleFolder;
	static SimpleLogger logger;

	static XCCConnectionProvider xcccp;

	// URI connectionUri = null;
	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setup() {
		logger = SimpleLogger.getSimpleLogger();
		xcccp = new XCCConnectionProvider(
				TestHelper.getCorbUnitTestConnectionUriWithModulesDb());
	}

	/*
	 * The following three tests (below) should all throw a RuntimeException as
	 * the Modules can't be located. This is correct behaviour for the
	 * configuration arguments passed in
	 */

	/**
	 * Unit test to prove the medline-iso8601.xqy arguments work
	 * 
	 * @throws URISyntaxException
	 */
	@Test(expected = RuntimeException.class)
	public void testFirstSampleInvocationWithoutSpecifyingTheCorrectModulesDbForTheApplicationServer() {
		invokeCorbWithArguments(TestHelper.getFirstSampleInvocation());
	}

	@Test(expected = RuntimeException.class)
	public void testSecondSampleInvocationWithoutSpecifyingTheCorrectModulesDbForTheApplicationServer() {
		invokeCorbWithArguments(TestHelper.getSecondSampleInvocation());
	}

	@Test(expected = RuntimeException.class)
	public void testThirdSampleInvocationWithoutSpecifyingTheCorrectModulesDbForTheApplicationServer() {
		invokeCorbWithArguments(TestHelper.getThirdSampleInvocation());
	}

	/*
	 * The following tests have flags to first install the modules
	 */

	// @Test(expected = com.marklogic.xcc.exceptions.XQueryException.class)
	// @Test(expected = RuntimeException.class)

	// @Rule
	// ublic Class<XQueryException> thrown =
	// com.marklogic.xcc.exceptions.XQueryException.class;

	// @ExpectedException(class=com.marklogic.xcc.exceptions.XQueryException.class,
	// message="Exception Message", causeException)
	@Test(expected = RuntimeException.class)
	public void testFirstSampleInvocationWithIncorrectModuleRootPath() {
		invokeCorbWithArguments(TestHelper
				.getFirstSampleInvocationWithCorrectXDBCModulesDatabase());
	}

	// @Test(expected = com.marklogic.xcc.exceptions.XQueryException.class)
	@Test(expected = RuntimeException.class)
	public void testSecondSampleInvocationWithIncorrectModuleRootPath() {
		invokeCorbWithArguments(TestHelper
				.getSecondSampleInvocationWithCorrectXDBCModulesDatabase());
	}

	/**
	 * These work correctly with the modified Module root (/corb/)
	 */

	@Test
	public void testFirstSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getFirstSampleInvocationWithCorrectXDBCModulesDatabaseAndModuleRoot());
	}

	@Test
	public void testSecondSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getSecondSampleInvocationWithCorrectXDBCModulesDatabaseAndModuleRoot());
	}

	@Test
	public void testThirdSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getThirdSampleInvocationWithFlagToCopyModules());
	}

	@Before
	public void clearModulesBeforeRunningEachTest() {
		logger.info("Clearing the Test Modules Database before");
		xcccp.buildConnection(TestHelper.UNIT_TEST_CLEANUP_MODULES_DB);
	}

	@Test
	public void testWithAllArgs() {
		invokeCorbWithArguments(TestHelper.getFullCorbArgs());
		assertEquals(
				"Ensuring the Modules have been deleted from the modules DB: ",
				"0", xcccp.getEstimatedDocsInDb());
	}

	@Test
	public void testWithAllArgsExceptDelete() {
		invokeCorbWithArguments(TestHelper.getFullCorbArgsWithoutDeleteFlag());
		assertEquals(
				"Ensuring the Modules have *NOT* deleted from the modules DB (there should be 2): ",
				"2", xcccp.getEstimatedDocsInDb());
	}

	private void invokeCorbWithArguments(String[] arguments) {
		logger.info("***** Corb task execution start *****");
		logger.info(MessageFormat.format("Starting CORB on: {0}", new Date()));
		logger.info("Currently running the test: " + name.getMethodName());
		try {
			Manager.main(arguments);
		} catch (URISyntaxException e) {
			logger.severe(e.getMessage());
		}
		logger.info("***** Corb task execution complete *****\n");
	}

}
