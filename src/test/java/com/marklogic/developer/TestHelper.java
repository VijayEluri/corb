package com.marklogic.developer;

/**
 * The <strong>TestHelper</strong> class contains static variables and methods to 
 * aid project testing.  It also contains the necessary configuration parameters
 * for enabling testing CORB under all the conditions provided in the original 
 * documentation, found at:
 * 
 * <strong>http://marklogic.github.com/corb/</strong>
 * 
 * The test configuration(s) provided by this helper class are designed to safeguard
 * the original features of CORB in an attempt to minimise the risk of introducing
 * new bugs into existing CORB code.
 * 
 * 	Wrapper / Helper class for getting sets of CORB argument parameters used mainly
 *  to aid testing
 * 
 * @author ableasdale
 * 
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.marklogic.xcc.Request;

public class TestHelper {

	/*
	 * public static final String HOME = System.getProperty("user.home"); public
	 * static final String TMP = System.getProperty("java.io.tmpdir");
	 */

	/**
	 * All Values below are applicable for the management of Setup / Teardown
	 * phases of all CORB Unit tests
	 */

	/* Connection URI management */
	private static final String BASE_CONNECTION_URI = "xcc://admin:admin@localhost";
	private static final String INITIAL_CONNECTION_PORT = "8010";
	private static final String TEST_APPSERVER_PORT = "9997";

	/* Unit test database and forest names */
	private static final String TEST_DB = "unit-test-db";
	private static final String TEST_DB_MODULES = "unit-test-modules";
	private static final String TEST_FOREST = "unit-test-forest01";
	private static final String TEST_FOREST_MODULES = "unit-test-modules-forest01";

	/* Standard CORB arguments expressed as booleans (as Strings) */
	private static final String REMOVE_MODULES_AFTER = "true";
	private static final String DO_NOT_REMOVE_MODULES_AFTER = "false";
	private static final String INSTALL_MODULES_ON_SERVER = "true";
	private static final String DO_NOT_INSTALL_MODULES_ON_SERVER = "false";

	/* Generic CORB Module root and default Collection arguments */
	private static final String MODULE_ROOT = "/";
	private static final String DEFAULT_COLLECTION = "";

	/* Unit test specific CORB arguments - used for running the test suite */
	public static final String UNIT_TEST_MODULE_ROOT = "src/main/resources/";
	public static final String UNIT_TEST_SETUP = UNIT_TEST_MODULE_ROOT
			+ "create-test-environment.xqy";
	public static final String UNIT_TEST_TEARDOWN = UNIT_TEST_MODULE_ROOT
			+ "teardown-test-environment.xqy";
	public static final String UNIT_TEST_POPULATE_DB = UNIT_TEST_MODULE_ROOT
			+ "populate-test-environment.xqy";
	public static final String UNIT_TEST_CLEANUP_MODULES_DB = UNIT_TEST_MODULE_ROOT
			+ "clean-db.xqy";

	/* Modules used for unit tests */
	public static final String BASIC_TRANSFORM_MODULE = "basic-transform-module.xqy";
	public static final String BASIC_URI_SELECTION_MODULE = "basic-uri-selection.xqy";

	/* Other CORB defaults */
	public static final String DEFAULT_THREADS = "16";
	private static final String NO_MODULES_DB_USING_FS_INSTEAD = "0";

	/**
	 * A bit overkill for CORBs purposes as all it really needs is the string as
	 * an argument - leaving this in here as there will be some initial test
	 * validation that the URI is structured correctly (URISyntaxException).
	 * 
	 * @return The Connection URI as a String
	 */
	private static String getConnectionUri(String uri) {
		URI connectionUri = null;
		try {
			connectionUri = new URI(uri);
		} catch (URISyntaxException e) {
			SimpleLogger.getSimpleLogger().severe(e.toString());
		}
		return connectionUri.toString();
	}

	/**
	 * Getter for the initial Connection URI for starting the unit test building
	 * process (Configured by the INITIAL_CONNECTION_PORT variable)
	 * 
	 * @return The Connection URI as a String
	 */
	public static String getConnectionUri() {
		return getConnectionUri(BASE_CONNECTION_URI + ":"
				+ INITIAL_CONNECTION_PORT);
	}

	/**
	 * Getter for the initial Connection URI for running the unit tests to
	 * ensure the code is healthy (Configured by the TEST_APPSERVER_PORT
	 * variable)
	 * 
	 * @return The Connection URI as a String
	 */
	public static String getCorbUnitTestConnectionUri() {
		return getConnectionUri(BASE_CONNECTION_URI + ":" + TEST_APPSERVER_PORT);
	}

	public static String getCorbUnitTestConnectionUriWithModulesDb() {
		return getConnectionUri(BASE_CONNECTION_URI + ":" + TEST_APPSERVER_PORT
				+ "/" + TEST_DB_MODULES);
	}

	/**
	 * Used to bind all the String and Integer variables required for building
	 * the test database(s) and module(s)
	 * 
	 * @param request
	 * @param startTeardown
	 * @return
	 */
	public static Request setTestConfigurationVariables(Request request,
			Boolean startTeardown) {
		request.setNewStringVariable("TEST_DB", TEST_DB);
		request.setNewStringVariable("TEST_DB_MODULES", TEST_DB_MODULES);
		request.setNewStringVariable("TEST_FOREST", TEST_FOREST);
		request.setNewStringVariable("TEST_FOREST_MODULES", TEST_FOREST_MODULES);
		// You can't bind a boolean in XCC at the moment - using xs:integer
		// instead
		if (startTeardown) {
			request.setNewIntegerVariable("BEGIN_TEARDOWN", 1);
		} else {
			request.setNewIntegerVariable("BEGIN_TEARDOWN", 0);
		}
		return request;
	}

	/*
	 * F I R S T S A M P L E I N V O C A T I O N (S)
	 */

	/**
	 * This is equivalent to the params passed to CORB in the example for the
	 * first invocation in CORBs readme:
	 * 
	 * <strong>java -cp $HOME/lib/java/xcc.jar:$HOME/lib/java/corb.jar \
	 * com.marklogic.developer.corb.Manager \ xcc://admin:admin@localhost:9002/
	 * "" \ medline-iso8601.xqy</strong>
	 * 
	 * @return
	 */
	public static String[] getFirstSampleInvocation() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add("medline-iso8601.xqy");
		return (args.toArray(new String[args.size()]));
	}

	public static String[] getFirstSampleInvocationWithCorrectXDBCModulesDatabase() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add("medline-iso8601.xqy");
		// Threads
		args.add("");
		// URI Selection Module
		args.add("");
		// Module root
		args.add("");
		// Modules DB
		args.add(TEST_DB_MODULES);
		return (args.toArray(new String[args.size()]));
	}

	/*
	 * S E C O N D S A M P L E I N V O C A T I O N (S)
	 */

	/**
	 * This is equivalent to the params passed to CORB in the example for the
	 * second invocation in CORBs readme:
	 * 
	 * java -cp $HOME/lib/java/xcc.jar:$HOME/lib/java/corb.jar \
	 * com.marklogic.developer.corb.Manager \ xcc://admin:admin@localhost:9002/
	 * "" \ /home/myproject/src/custom-transform.xqy 2 \
	 * /home/myproject/src/custom-uri-selection.xqy
	 * 
	 * @return
	 */
	public static String[] getSecondSampleInvocation() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		return (args.toArray(new String[args.size()]));
	}

	public static String[] getSecondSampleInvocationWithCorrectXDBCModulesDatabase() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add("");
		// Modules DB
		args.add(TEST_DB_MODULES);
		return (args.toArray(new String[args.size()]));
	}

	/*
	 * T H I R D S A M P L E I N V O C A T I O N (S)
	 */

	/**
	 * 
	 * A third sample invocation. Using 4 threads, custom modules pre-installed
	 * in the 'mydb' database processing with
	 * /preprocessing/custom-transform.xqy and using URIs returned by
	 * /preprocessing/custom-uri-selection.xqy:
	 * 
	 * java -cp $HOME/lib/java/xcc.jar:$HOME/lib/java/corb.jar \
	 * com.marklogic.developer.corb.Manager \ xcc://admin:admin@localhost:9002/
	 * "" \ custom-transform.xqy 4 \ custom-uri-selection.xqy \ /preprocessing/
	 * \ mydb false
	 */
	public static String[] getThirdSampleInvocation() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add(MODULE_ROOT);
		// Modules DB
		args.add(TEST_DB_MODULES);
		// Install
		args.add(DO_NOT_INSTALL_MODULES_ON_SERVER);
		return (args.toArray(new String[args.size()]));
	}

	public static String[] getThirdSampleInvocationWithFlagToCopyModules() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add(MODULE_ROOT);
		// Modules DB
		args.add(TEST_DB_MODULES);
		// Install
		// args.add(INSTALL_MODULES_ON_SERVER); // true by default
		return (args.toArray(new String[args.size()]));
	}

	/**
	 * A full set of CORB Arguments for more in-depth unit testing and for
	 * creating conditions where new functionality can be tested.
	 * 
	 * @return
	 */
	public static String[] getFullCorbArgs() {
		List<String> args = new ArrayList<String>();

		/**
		 * com.marklogic.developer.corb.Manager xcc://user:password@host:port/[
		 * database ] input-selector module-name.xqy [ thread-count [
		 * uris-module [ module-root [ modules-database [ install ] ] ] ] ]
		 */
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add(MODULE_ROOT);
		// Modules DB
		args.add(TEST_DB_MODULES);
		// Install
		args.add(INSTALL_MODULES_ON_SERVER);
		// Remove modules after (*one of the newly requested features*)
		args.add(REMOVE_MODULES_AFTER);
		return (args.toArray(new String[args.size()]));
	}

	public static String[] getFullCorbArgsWithoutDeleteFlag() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add(MODULE_ROOT);
		// Modules DB
		args.add(TEST_DB_MODULES);
		// Install
		args.add(INSTALL_MODULES_ON_SERVER);
		// Remove modules after (*one of the newly requested features*)
		args.add(DO_NOT_REMOVE_MODULES_AFTER);
		return (args.toArray(new String[args.size()]));
	}

	public static String[] getFullCorbArgsWithEmptyDeleteFlag() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add(MODULE_ROOT);
		// Modules DB
		args.add(TEST_DB_MODULES);
		// Install
		args.add(INSTALL_MODULES_ON_SERVER);
		// Remove modules after (*one of the newly requested features*)
		args.add("");
		return (args.toArray(new String[args.size()]));
	}
	
	public static String[] getFullCorbArgsWithoutInstallingModules() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getCorbUnitTestConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add(BASIC_TRANSFORM_MODULE);
		// Threads
		args.add(DEFAULT_THREADS);
		// URI Selection module
		args.add(BASIC_URI_SELECTION_MODULE);
		// Module root
		args.add(MODULE_ROOT);
		// Modules DB
		args.add(TEST_DB_MODULES);
		// Install
		args.add(DO_NOT_INSTALL_MODULES_ON_SERVER);
		// Remove modules after (*one of the newly requested features*)
		args.add("");
		return (args.toArray(new String[args.size()]));
	}

}