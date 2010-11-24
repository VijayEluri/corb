package com.marklogic.developer.corb;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.developer.SimpleLogger;
import com.marklogic.developer.TestHelper;

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

	// URI connectionUri = null;

	@BeforeClass
	public static void setup() {
		logger = SimpleLogger.getSimpleLogger();
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
	public void testFirstSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getFirstSampleInvocationWithFlagToCopyModules());
	}

	@Test
	public void testSecondSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getSecondSampleInvocationWithFlagToCopyModules());
	}

	@Test
	public void testThirdSampleInvocation() {
		invokeCorbWithArguments(TestHelper
				.getThirdSampleInvocationWithFlagToCopyModules());
	}

	@Test
	public void testWithAllArgs() {
		invokeCorbWithArguments(TestHelper.getFullCorbArgs());
	}

	private void invokeCorbWithArguments(String[] arguments) {
		logger.info(MessageFormat.format("Starting CORB on: {0}", new Date()));

		try {
			Manager.main(arguments);
		} catch (URISyntaxException e) {
			logger.severe(e.getMessage());
		}
		logger.info("***** Corb task execution complete *****");
	}
}
