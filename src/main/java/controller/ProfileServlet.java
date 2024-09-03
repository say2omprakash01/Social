package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.PostDAO;
import dao.UserDAO;
import model.Post;
import model.User;

/**
 * Servlet implementation class ProfileServlet
 */
@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ProfileServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login");
            return;
        }

        PostDAO postDAO = new PostDAO();
        ArrayList<Post> posts = new ArrayList<>();
        try {
            posts = postDAO.getUserPost((int) session.getAttribute("user_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("posts", posts);
        request.getRequestDispatcher("WEB-INF/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login");
            return;
        }

        String type = request.getParameter("type");
        if (type == null) {
            response.sendRedirect("profile");
            return;
        }

        switch (type) {
            case "change_profile":
                updateProfile(request, response, session);
                break;
            case "change_password":
                changePassword(request, response, session);
                break;
            case "update_post":
                updatePost(request, response, session);
                break;
            default:
                response.sendRedirect("profile");
        }
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String first_name = request.getParameter("first_name").trim();
        String last_name = request.getParameter("last_name").trim();
        String email = request.getParameter("email").trim();

        if (first_name.isEmpty() || last_name.isEmpty() || email.isEmpty()) {
            request.setAttribute("pmsg", "All fields are required.");
            doGet(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = new User();
        user.setUser_id((int) session.getAttribute("user_id"));
        user.setFirst_name(first_name);
        user.setEmail(email);
        user.setLast_name(last_name);
        String result = userDAO.updateProfile(user);
        request.setAttribute("pmsg", result);
        doGet(request, response);
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String password = request.getParameter("password").trim();
        String cpassword = request.getParameter("cpassword").trim();

        if (password.isEmpty() || cpassword.isEmpty()) {
            request.setAttribute("pmsg", "All fields are required.");
            doGet(request, response);
            return;
        }

        if (!password.equals(cpassword)) {
            request.setAttribute("pmsg", "Passwords do not match.");
            doGet(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = new User();
        user.setUser_id((int) session.getAttribute("user_id"));
        user.setPassword(password);
        String result = userDAO.updatePassword(user);
        request.setAttribute("pmsg", result);
        doGet(request, response);
    }

    private void updatePost(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String post = request.getParameter("post").trim();
        String post_id = request.getParameter("post_id");

        if (post.isEmpty()) {
            request.setAttribute("pmsg", "Post message is required.");
            doGet(request, response);
            return;
        }

        try {
            PostDAO postDAO = new PostDAO();
            Post postObject = postDAO.getPost(Integer.parseInt(post_id));
            if (postObject != null) {
                postObject.setBody(post);
                String updateResult = postDAO.updatePost(postObject);
                request.setAttribute("pmsg", updateResult);
            } else {
                request.setAttribute("pmsg", "Failed to update post.");
            }
        } catch (SQLException e) {
            request.setAttribute("pmsg", "Database error occurred while updating post.");
            e.printStackTrace();
        }
        doGet(request, response);
    }
}