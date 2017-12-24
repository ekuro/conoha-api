import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Created by ekuro on 2017/12/24.
 */
@Getter
class AccessToken {

    private Access access;

    String getTokenId() {
        return access.getToken().getId();
    }

    @Getter
    private class Access {
        private Token token;
    }

    @Getter
    private class Token {
        private LocalDateTime issued_at;
        private LocalDateTime expires;
        private String id;
    }
}
