import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private Map<String, String> userCredentials;
    private Map<String, UserRole> userRoles;

    public AuthService() {
        userCredentials = new HashMap<>();
        userRoles = new HashMap<>();

        userCredentials.put("admin", "adminpass");
        userRoles.put("admin", UserRole.ADMIN);

        userCredentials.put("teacher", "teacherpass");
        userRoles.put("teacher", UserRole.TEACHER);

        userCredentials.put("student", "studentpass");
        userRoles.put("student", UserRole.STUDENT);
    }

    public UserRole authenticate(String username, String password) {
        String storedPassword = userCredentials.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            return userRoles.get(username);
        }
        return null;
    }
}