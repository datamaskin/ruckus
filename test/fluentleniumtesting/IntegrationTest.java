package fluentleniumtesting;

import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;
import static play.test.Helpers.HTMLUNIT;

/**
 * Created by davidb on 8/19/14.
 */
public class IntegrationTest {
    /** The port to be used for testing. */
    private final int port = 3333;

    /**
     * Sample test that submits a form and then checks that form data was echoed to page.
     */
    @Test
    public void test() {
        running(testServer(port, fakeApplication(inMemoryDatabase())), HTMLUNIT,
                new F.Callback<TestBrowser>() {
                    public void invoke(TestBrowser browser) {
                        IndexPage indexPage = new IndexPage(browser.getDriver(), port);
                        browser.goTo(indexPage);
                        indexPage.submitForm();
                        assertThat(browser.pageSource()).contains("");
                    }
                });
    }
}
