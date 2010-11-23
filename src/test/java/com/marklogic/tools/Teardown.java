package com.marklogic.tools;

import com.marklogic.developer.TestHelper;
import com.marklogic.developer.XCCConnectionProvider;
import com.marklogic.xcc.ContentSource;

/**
 * This can be used if something happens and the teardown doesn't seem to
 * complete as we would have hoped... It shouldn't be required if things are
 * running smoothly.
 * 
 * @author ableasdale
 * 
 */

public class Teardown {
	/**
	 * Hit the main method if it looks like you're having problems cleaning up
	 * after running the unit tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		XCCConnectionProvider xcccp = new XCCConnectionProvider(
				TestHelper.getConnectionUri());
		ContentSource cs = xcccp.getContentSource();
		xcccp.buildConnection(cs, TestHelper.UNIT_TEST_TEARDOWN, true);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		xcccp.buildConnection(cs, TestHelper.UNIT_TEST_TEARDOWN, false);
	}
}
