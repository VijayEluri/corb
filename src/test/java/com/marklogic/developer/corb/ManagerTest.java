package com.marklogic.developer.corb;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

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

	static SimpleLogger logger;
	static XCCConnectionProvider xcccp;

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setup() {
		logger = SimpleLogger.getSimpleLogger();
		xcccp = new XCCConnectionProvider(
				TestHelper.getCorbUnitTestConnectionUriWithModulesDb());
	}

	/*
	 * Before each unit test clear the [unit test] modules-db folder
	 */
	@Before
	public void clearModulesBeforeRunningEachTest() {
		logger.info("Clearing the Test Modules Database before");
		xcccp.buildConnection(TestHelper.UNIT_TEST_CLEANUP_MODULES_DB);
	}

	/*
	 * The following three tests (below) should all throw a RuntimeException as
	 * the Modules can't be located. This is correct behaviour for the
	 * configuration arguments passed in.
	 */

	/**
	 * Unit test to prove the medline-iso8601.xqy arguments work
	 * 
	 * @throws URISyntaxException
	 */
	@Test(expected = RuntimeException.class)
	public void testFirstSampleInvocationWithoutSpecifyingTheCorrectModulesDbForTheApplicationServer()
			throws Exception {
		try {
			invokeCorbWithArguments(TestHelper.getFirstSampleInvocation());
		} catch (Exception e) {
			assertEquals(
					"Should throw a permissions error: ",
					"com.marklogic.xcc.exceptions.XQueryException: SEC-PERMDENIED: Permission denied\nin /insert",
					e.getLocalizedMessage());
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void testSecondSampleInvocationWithoutSpecifyingTheCorrectModulesDbForTheApplicationServer()
			throws Exception {
		try {
			invokeCorbWithArguments(TestHelper.getSecondSampleInvocation());
		} catch (Exception e) {
			assertEquals(
					"Should throw a SEC-URIPRIV: ",
					"com.marklogic.xcc.exceptions.XQueryException: SEC-URIPRIV: URI privilege required\nin /insert",
					e.getLocalizedMessage());
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void testThirdSampleInvocationWithoutSpecifyingTheCorrectModulesDbForTheApplicationServer()
			throws Exception {
		try {
			invokeCorbWithArguments(TestHelper.getThirdSampleInvocation());
		} catch (Exception e) {
			assertEquals(
					"Should throw an XDMP-TEXTNODE: ",
					"com.marklogic.xcc.exceptions.XQueryException: XDMP-TEXTNODE: /corb/basic-uri-selection.xqy -- Server unable to build program from non-text document\nin /corb/basic-uri-selection.xqy\nexpr: /corb/basic-uri-selection.xqy",
					e.getLocalizedMessage());
			throw e;
		}
	}

	/*
	 * The following tests have flags to first install the modules
	 */
	@Test(expected = RuntimeException.class)
	public void testFirstSampleInvocationWithIncorrectModuleRootPath()
			throws Exception {
		try {
			invokeCorbWithArguments(TestHelper
					.getFirstSampleInvocationWithCorrectXDBCModulesDatabase());
		} catch (Exception e) {
			assertEquals(
					"Should throw a SEC-URIPRIV: ",
					"com.marklogic.xcc.exceptions.XQueryException: SEC-URIPRIV: URI privilege required\nin /insert",
					e.getLocalizedMessage());
			throw e;
		}
	}

	@Test(expected = RuntimeException.class)
	public void testSecondSampleInvocationWithIncorrectModuleRootPath()
			throws Exception {
		try {
			invokeCorbWithArguments(TestHelper
					.getSecondSampleInvocationWithCorrectXDBCModulesDatabase());
		} catch (Exception e) {
			assertEquals(
					"Should throw a SEC-URIPRIV: ",
					"com.marklogic.xcc.exceptions.XQueryException: SEC-URIPRIV: URI privilege required\nin /insert",
					e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * These work correctly with the modified Module root (/corb/)
	 */

	@Test
	public void testFirstSampleInvocation() throws Exception {
		invokeCorbWithArguments(TestHelper
				.getFirstSampleInvocationWithCorrectXDBCModulesDatabaseAndModuleRoot());

	}

	@Test
	public void testSecondSampleInvocation() throws Exception {
		invokeCorbWithArguments(TestHelper
				.getSecondSampleInvocationWithCorrectXDBCModulesDatabaseAndModuleRoot());
	}

	@Test
	public void testThirdSampleInvocation() throws Exception {
		invokeCorbWithArguments(TestHelper
				.getThirdSampleInvocationWithFlagToCopyModules());
	}

	@Test
	public void testWithAllArgs() throws Exception {
		invokeCorbWithArguments(TestHelper.getFullCorbArgs());
		assertEquals(
				"Ensuring the Modules have been deleted from the modules DB: ",
				"0", xcccp.getEstimatedDocsInDb());
	}

	@Test
	public void testWithAllArgsExceptDelete() throws Exception {
		invokeCorbWithArguments(TestHelper.getFullCorbArgsWithoutDeleteFlag());
		assertEquals(
				"Ensuring the Modules have *NOT* deleted from the modules DB (there should be 2): ",
				"2", xcccp.getEstimatedDocsInDb());
	}

	@Test
	public void testWithAnEmptyStringForTheModuleDeleteArgument()
			throws Exception {
		invokeCorbWithArguments(TestHelper.getFullCorbArgsWithEmptyDeleteFlag());
		assertEquals(
				"Ensuring the Modules have *NOT* deleted from the modules DB (there should be 2): ",
				"2", xcccp.getEstimatedDocsInDb());
	}

	@Test(expected = RuntimeException.class)
	public void testNotInstallingModulesFirst() throws Exception {
		assertEquals(
				"Ensuring the Modules have been deleted from the modules DB: ",
				"0", xcccp.getEstimatedDocsInDb());
		try {
			invokeCorbWithArguments(TestHelper
					.getFullCorbArgsWithoutInstallingModules());
		} catch (Exception e) {
			assertEquals(
					"Should throw an XDMP-TEXTNODE: ",
					"com.marklogic.xcc.exceptions.XQueryException: XDMP-TEXTNODE: /corb/basic-uri-selection.xqy -- Server unable to build program from non-text document\nin /corb/basic-uri-selection.xqy\nexpr: /corb/basic-uri-selection.xqy",
					e.getLocalizedMessage());
			throw e;
		}
	}

	private void invokeCorbWithArguments(String[] arguments) throws Exception {
		logger.info("\nCurrently running the test: " + name.getMethodName());
		Manager.main(arguments);
	}
}