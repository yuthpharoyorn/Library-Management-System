import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public static User getUserFromDatabase(String enteredUsername) {
        
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, enteredUsername);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String role = resultSet.getString("role");

                    return new User(username, password, role);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return null; 
    }
    public boolean login(String enteredUsername, String enteredPassword, String enteredRole) {
        User userFromDatabase = getUserFromDatabase(enteredUsername);

        
        if (userFromDatabase != null && userFromDatabase.getPassword().equals(enteredPassword)) {
           
            return userFromDatabase.getRole().equals(enteredRole);
        }

        return false;
    


    }
}
