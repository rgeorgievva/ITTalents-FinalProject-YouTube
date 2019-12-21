import model.db.DBManager;
import model.user.User;
import model.user.UserDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class Demo {

    public static void main(String[] args) {
        User user = new User("rgeorgieva", "Radostina", "Georgieva", "rg@gmail.com", "test");

        try {
            UserDAO.getInstance().registerUser(user);
            UserDAO.getInstance().loginUser("rg@gmail.com", "test1");
        } catch (SQLException e) {
            System.out.println("Unable to register user.");
        }
    }
}
