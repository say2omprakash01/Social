import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import model.User;
import dao.UserDAO;

@WebServlet("/FriendSearchServlet")
public class FriendSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("query");
        UserDAO userDAO = new UserDAO();
        ArrayList<User> users = null;
        try {
            users = userDAO.getUserByName(query); // Correct method name as per your DAO
        } catch (SQLException e) {
            response.getWriter().write("[]"); // Return an empty JSON array in case of SQL error
            return;
        }

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            jsonBuilder.append("{");
            jsonBuilder.append("\"userId\":").append(user.getUser_id()).append(",");
            jsonBuilder.append("\"firstName\":\"").append(user.getFirst_name()).append("\",");
            jsonBuilder.append("\"lastName\":\"").append(user.getLast_name()).append("\"");
            jsonBuilder.append("}");
            if (i < users.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        response.getWriter().write(jsonBuilder.toString());
    }
}