import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by ekuro on 2017/12/24.
 */
@Getter
class ConohaUser {

    private PasswordCredentials passwordCredentials;
    private String tenantId;

    ConohaUser(String username, String password, String tenantId) {
        this.passwordCredentials = new PasswordCredentials(username, password);
        this.tenantId = tenantId;
    }

    @Getter
    @AllArgsConstructor
    private class PasswordCredentials {
        private String username;
        private String password;
    }
}
