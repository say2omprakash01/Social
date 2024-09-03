package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.PostDAO;
import model.Post;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public HomeServlet() {
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
            posts = postDAO.getAllPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("posts", posts);

        request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        int userId = (int) session.getAttribute("user_id");

        if ("post".equals(action)) {
            String postContent = request.getParameter("post").trim();
            if (!postContent.equals("")) {
                try {
                    PostDAO postDAO = new PostDAO();
                    postDAO.insertPost(userId, postContent);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if ("like".equals(action)) {
            int postId = Integer.parseInt(request.getParameter("post_id"));
            try {
                PostDAO postDAO = new PostDAO();
                postDAO.addLike(postId, userId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if ("comment".equals(action)) {
            int postId = Integer.parseInt(request.getParameter("post_id"));
            String comment = request.getParameter("comment");
            try {
                PostDAO postDAO = new PostDAO();
                postDAO.addComment(postId, userId, comment);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("home");
    }
}