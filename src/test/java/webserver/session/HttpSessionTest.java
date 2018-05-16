package webserver.session;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpSessionTest {
    private HttpSession sut;

    @Before
    public void setUp() {
        sut = HttpSession.newInstance("test-session-id");
    }

    @Test
    public void getIdTest() {
        assertThat(sut.getId()).isEqualTo("test-session-id");
    }

    @Test
    public void setAttributeTest() {
        sut.setAttribute("name", "june");
    }

    @Test
    public void getAttributeTest() {
        sut.setAttribute("name", "june");
        assertThat(sut.getAttribute("name")).isEqualTo("june");
    }

    @Test
    public void removeAttributeTest() {
        sut.setAttribute("name", "june");
        sut.removeAttribute("name");
        assertThat(sut.getAttribute("name")).isNull();
    }

    @Test
    public void invalidateTest() {
        sut.invalidate();
    }
}