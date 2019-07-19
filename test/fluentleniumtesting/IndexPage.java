package fluentleniumtesting;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.WebDriver;

/**
 * Created by davidb on 8/19/14.
 */
public class IndexPage extends FluentPage {
    private String url;

    /**
     * Create the IndexPage.
     * @param webDriver The driver.
     * @param port The port.
     */
    public IndexPage(WebDriver webDriver, int port) {
        super(webDriver);
        this.url = "http://localhost:" + port + "/admin";
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void isAt() {
        assert (title().equals("Index"));
    }

    public void submitForm() {
        // Fill the input field with id "name" with the passed name string.
        findFirst("#checkbox").click();
        submit("#submit");
    }
}
