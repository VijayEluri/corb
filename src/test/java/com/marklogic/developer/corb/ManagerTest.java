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
	 * configuration arguments passed in
	 */

	/**
	 * Unit test to prove the medline-iso8601.xqy arguments work
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

	@Test
	public void testFirstSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getFirstSampleInvocationWithCorrectXDBCModulesDatabase());
	}

	@Test
	public void testSecondSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getSecondSampleInvocationWithCorrectXDBCModulesDatabase());
	}

	@Test
	public void testThirdSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getThirdSampleInvocationWithFlagToCopyModules());
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
	
	@Test 
	public void testWithAnEmptyStringForTheModuleDeleteArgument(){
		invokeCorbWithArguments(TestHelper.getFullCorbArgsWithEmptyDeleteFlag());
		assertEquals(
				"Ensuring the Modules have *NOT* deleted from the modules DB (there should be 2): ",
				"2", xcccp.getEstimatedDocsInDb());
	}
	
	@Test(expected = RuntimeException.class)
	public void testNotInstallingModulesFirst(){
		assertEquals(
				"Ensuring the Modules have been deleted from the modules DB: ",
				"0", xcccp.getEstimatedDocsInDb());
		invokeCorbWithArguments(TestHelper.getFullCorbArgsWithoutInstallingModules());
	}

	private void invokeCorbWithArguments(String[] arguments) {
		logger.fine("***** Corb task execution start *****");
		logger.fine(MessageFormat.format("Starting CORB on: {0}", new Date()));
		logger.info(partition() + name.getMethodName()
				+ " being run..." + partition());
		try {
			Manager.main(arguments);
		} catch (URISyntaxException e) {
			logger.severe(e.getMessage());
		}
		logger.fine("***** Corb task execution complete *****\n");
	}
	
	private String partition(){
		return "\n**********************************************************************************\n";
	}

}
