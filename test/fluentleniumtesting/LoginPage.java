package fluentleniumtesting;

import org.fluentlenium.core.FluentPage;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by davidb on 8/18/14.
 */
public class LoginPage extends FluentPage {

    @Test
    public String getUrl() {
        return "http://local.victiv.com:9000/auth/login";
    }

    @Test
    public void isAt() {
        assertThat(title()).isEqualTo("SIGN IN");
    }
    /*public void fillAndSubmitForm(String... paramsOrdered) {
        fill("input").with(paramsOrdered);
        click("#create-button");
    }*/

}
