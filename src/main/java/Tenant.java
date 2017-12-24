/**
 * Created by ekuro on 2017/12/24.
 */
class Tenant {
    ConohaUser auth;
    Tenant(ConohaUser user) {
        this.auth = user;
    }
}
