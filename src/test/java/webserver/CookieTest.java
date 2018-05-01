package webserver;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CookieTest {
    private Cookie sut;

    @Before
    public void setUp() {
        sut = Cookie.create();
        sut.addCookie("name1", "value1");
        sut.addCookie("name2", "value2");
    }

    @Test
    public void testCreation() {
        assertThat(sut).isNotNull();
    }

    @Test
    public void setCookieTest() {
        sut.addCookie("login", "false");
    }

    @Test
    public void getCookieTest() {
        assertThat(sut.getCookie("name1")).isEqualTo("value1");
        assertThat(sut.getCookie("login")).isEqualTo("");
    }

    @Test
    public void getCookieResponseHeaderTest() {
        final String result = sut.getCookieResponseHeader();

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).contains("Set-Cookie: name1=value1 \r\n", "Set-Cookie: name1=value1 \r\n");
    }
}