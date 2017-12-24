import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ekuro on 2017/12/24.
 */
@Getter
@AllArgsConstructor
class Servers {
    private List<Server> servers;
    @Getter
    class Server {
        private String id;
        private String name;
    }
    Stream<Server> stream() {
        return servers.stream();
    }
    boolean isEmpty() {
        return Objects.isNull(servers) || servers.size() == 0;
    }
    boolean contains(Server target) {
        return stream().anyMatch(server -> server.getId().equals(target.getId()));
    }
    Servers remove(Servers target) {
        return new Servers(
                stream()
                        .filter(server -> !target.contains(server))
                        .collect(Collectors.toList()));
    }
}
