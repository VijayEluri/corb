package com.marklogic.developer.corb;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
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
	SimpleLogger logger;

	// URI connectionUri = null;

	@Before
	public void setup() {
		logger = SimpleLogger.getSimpleLogger();
		logger.info("Setting up unit test");
		// All Setup moved to TestHelper for now
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

	@After
	public void tearDown() {
		logger.info("Tearing down unit test");
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
