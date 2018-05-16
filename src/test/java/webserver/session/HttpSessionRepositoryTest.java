package webserver.session;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpSessionRepositoryTest {

    @Test
    public void getSession() {
        final HttpSession session = HttpSessionRepository.getSession("1");

        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo("1");
    }

    @Test
    public void remove() {
        HttpSession oldSession = HttpSessionRepository.getSession("1");
        HttpSession newSession = HttpSessionRepository.getSession("1");
        assertThat(oldSession).isEqualTo(newSession);

        HttpSessionRepository.remove("1");
        newSession = HttpSessionRepository.getSession("1");
        assertThat(oldSession).isNotEqualTo(newSession);
    }
}