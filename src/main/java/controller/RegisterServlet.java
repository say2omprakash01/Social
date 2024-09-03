package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user_id") != null) {
            response.sendRedirect("home");
        } else {
            request.setAttribute("page", "register");
            request.getRequestDispatcher("WEB-INF/login.jsp").forward(request, response);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String first_name = request.getParameter("first_name").trim();
        String last_name = request.getParameter("last_name").trim();
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();
        String cpassword = request.getParameter("cpassword").trim();

        boolean status = true;
     


        if(!status) {
            request.setAttribute("page", "register");
            request.getRequestDispatcher("WEB-INF/login.jsp").forward(request, response);
        } else {
            UserDAO userDAO = new UserDAO();
            User user = new User();
            user.setPassword(password);
            user.setFirst_name(first_name);
            user.setEmail(email);
            user.setLast_name(last_name);
            String result = userDAO.register(user);
            request.setAttribute("rmsg", result);
            request.setAttribute("page", "register");
            request.getRequestDispatcher("WEB-INF/login.jsp").forward(request, response);
        }
    }
}