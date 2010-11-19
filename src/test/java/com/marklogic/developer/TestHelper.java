package com.marklogic.developer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {

	/**
	 * Global Variables for easy project testing
	 */
	/*
	 * public static final String HOME = System.getProperty("user.home"); public
	 * static final String TMP = System.getProperty("java.io.tmpdir");
	 */

	// public static final String CORB_MODULE_FOLDER = "\\main\\resources\\";

	/**
	 * Values below are bound to the setup / teardown CORB Unit tests
	 */
	private static final String TEST_DB = "unit-test-db";
	private static final String TEST_DB_MODULES = "unit-test-modules";
	private static final String TEST_FOREST = "unit-test-forest01";
	private static final String TEST_FOREST_MODULES = "unit-test-modules-forest01";

	private static final String REMOVE_MODULES_AFTER = "false";
	private static final String INSTALL_MODULES_ON_SERVER = "true";
	private static final String DO_NOT_INSTALL_MODULES_ON_SERVER = "false";

	private static final String MODULES_DB = "Modules";
	private static final String MODULE_ROOT = "/";
	private static final String DEFAULT_COLLECTION = "";

	public static final String BASIC_TRANSFORM_MODULE = "basic-transform-module.xqy";
	public static final String BASIC_URI_SELECTION_MODULE = "basic-uri-selection.xqy";

	public static final String DEFAULT_THREADS = "5";
	private static final String NO_MODULES_DB_USING_FS_INSTEAD = "0";
	private static final String ALT_MODULES_DB = "mydb";

	/**
	 * A bit overkill for CORBs purposes as all it really needs is the string as
	 * an argument - leaving this in here as there will be some initial test
	 * validation that the URI is structured correctly.
	 * 
	 * @return
	 */
	public static String getConnectionUri() {
		URI connectionUri = null;
		try {
			// TODO - Refactor these values out to a config file?
			connectionUri = new URI("xcc://admin:admin@localhost:8003");
		} catch (URISyntaxException e) {
			SimpleLogger.getSimpleLogger().severe(e.toString());
		}
		return connectionUri.toString();
	}

	/**
	 * This is equivalent to the params passed to CORB in the example for the
	 * first invocation in CORBs readme:
	 * 
	 * java -cp $HOME/lib/java/xcc.jar:$HOME/lib/java/corb.jar \
	 * com.marklogic.developer.corb.Manager \ xcc://admin:admin@localhost:9002/
	 * "" \ medline-iso8601.xqy
	 * 
	 * @return
	 */
	public static String[] getFirstSampleInvocation() {
		List<String> args = new ArrayList<String>();
		// URI
		args.add(getConnectionUri());
		// Collection
		args.add(DEFAULT_COLLECTION);
		// XQ Transform Module
		args.add("medline-iso8601.xqy");
		return (args.toArray(new String[args.size()]));
	}

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
		args.add(getConnectionUri());
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
		args.add(getConnectionUri());
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
		args.add(ALT_MODULES_DB);
		// Install
		args.add(DO_NOT_INSTALL_MODULES_ON_SERVER);
		return (args.toArray(new String[args.size()]));
	}

	/**
	 * Wrapper / Helper class for getting sets of CORB argument parameters used
	 * mainly to aid testing
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
		args.add(getConnectionUri());
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
		args.add(MODULES_DB);
		// Install
		args.add(INSTALL_MODULES_ON_SERVER);
		// Remove modules after
		args.add(REMOVE_MODULES_AFTER);
		return (args.toArray(new String[args.size()]));
	}
}

// Module root
// args.add("/");
// args.add("Modules");

// args.add(INSTALL_MODULES_ON_SERVER);
/*
 * WE SHOULDN"T NEED TO CARE ABOUT THIS // Module root args.add(MODULE_ROOT); //
 * Modules DB args.add(NO_MODULES_DB); // # MODULES-DATABASE (uses the
 * XCC-CONNECTION-URI if not provided; use // 0 for filesystem)
 * args.add(INSTALL_MODULES_ON_SERVER);
 * 
 * // # INSTALL (default is true; set to 'false' or '0' to skip // installation)
 */
