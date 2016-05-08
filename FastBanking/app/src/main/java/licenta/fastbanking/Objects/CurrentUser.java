package licenta.fastbanking.Objects;

/**
 * Created by Tiberiu Visan on 5/7/2016.
 * Project: FastBanking
 */
public class CurrentUser {

    public String username;
    public boolean admin;

    @Override
    public String toString() {
        return "CurrentUser{" +
                "username='" + username + '\'' +
                ", admin=" + admin +
                '}';
    }
}
