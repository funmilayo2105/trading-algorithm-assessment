package codingblackfemales.gettingstarted;
import java.util.List;



public class User {

    private String username;
    private List<String> roles;

    public User(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
    

    // Check if the user has a specific role or permission
    public boolean hasPermission(String permission) {
        return roles.contains(permission);
    }
}
