import com.marklogic.developer.TestHelper;
import com.marklogic.developer.XCCConnectionProvider;
import com.marklogic.xcc.ContentSource;

/**
 * TODO - write proper tests or mark for deprecation
 * 
 * @author ableasdale
 * 
 */

public class TestXCCConnectionProvider {
	public static void main(String[] args) {
		XCCConnectionProvider xcccp = new XCCConnectionProvider(
				TestHelper.getConnectionUri());
		ContentSource cs = xcccp.getContentSource();
		xcccp.buildConnection(cs, TestHelper.UNIT_TEST_TEARDOWN);

		/*
		 * String mod = ""; try { mod =
		 * xcccp.readFile(TestHelper.UNIT_TEST_TEARDOWN); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * SimpleLogger.getSimpleLogger().info(mod);
		 */
	}
}
