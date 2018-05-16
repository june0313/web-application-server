package verification;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class MapTest {

    @Test
    public void computeIfAbsent() {
        final HashMap<Integer, String> map = Maps.newHashMap();
        map.put(1, "first");
        final String firstResult = map.computeIfAbsent(1, key -> "new value");
        final String secondResult = map.computeIfAbsent(2, key -> "new value");

        assertThat(firstResult).isEqualTo("first");
        assertThat(secondResult).isEqualTo("new value");
        assertThat(map).containsKey(1);
        assertThat(map).containsEntry(1, "first");
        assertThat(map).containsEntry(2, "new value");
    }
}
