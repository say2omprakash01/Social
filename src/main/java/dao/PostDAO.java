package dao;
import model.Comment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Post;
import util.DBConnection;

public class PostDAO {

	public void insertPost(int user_id, String body) throws SQLException {
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement st = conn.prepareStatement("INSERT INTO post(user_id, body) VALUES (?,?);");
		st.setInt(1, user_id);
		st.setString(2, body);
		st.executeUpdate();
	}
	public void addLike(int post_id, int user_id) throws SQLException {
	    Connection conn = DBConnection.getInstance().getConnection();
	    PreparedStatement st = conn.prepareStatement("INSERT INTO likes(post_id, user_id) VALUES (?, ?);");
	    st.setInt(1, post_id);
	    st.setInt(2, user_id);
	    st.executeUpdate();
	}

	public int countLikes(int post_id) throws SQLException {
	    Connection conn = DBConnection.getInstance().getConnection();
	    PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) AS like_count FROM likes WHERE post_id = ?;");
	    st.setInt(1, post_id);
	    ResultSet rs = st.executeQuery();
	    if (rs.next()) {
	        return rs.getInt("like_count");
	    }
	    return 0;
	}

	public void addComment(int post_id, int user_id, String comment) throws SQLException {
	    Connection conn = DBConnection.getInstance().getConnection();
	    PreparedStatement st = conn.prepareStatement("INSERT INTO comments(post_id, user_id, comment) VALUES (?, ?, ?);");
	    st.setInt(1, post_id);
	    st.setInt(2, user_id);
	    st.setString(3, comment);
	    st.executeUpdate();
	}

	public ArrayList<Comment> getComments(int post_id) throws SQLException {
	    ArrayList<Comment> comments = new ArrayList<>();
	    Connection conn = DBConnection.getInstance().getConnection();
	    PreparedStatement st = conn.prepareStatement("SELECT * FROM comments WHERE post_id = ? ORDER BY comment_time DESC;");
	    st.setInt(1, post_id);
	    ResultSet rs = st.executeQuery();
	    while (rs.next()) {
	        Comment comment = new Comment();
	        comment.setCommentId(rs.getInt("comment_id"));
	        comment.setPostId(rs.getInt("post_id"));
	        comment.setUserId(rs.getInt("user_id"));
	        comment.setComment(rs.getString("comment"));
	        comment.setCommentTime(rs.getTimestamp("comment_time"));
	        comments.add(comment);
	    }
	    return comments;
	}
	public ArrayList<Post> getAllPost() throws SQLException {
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement st = conn.prepareStatement("SELECT * FROM post ORDER BY post_time DESC;");
		ResultSet rs = st.executeQuery();
		ArrayList<Post> array = new ArrayList<>();
		while(rs.next()) {
			Post p = new Post();
			p.setPost_id(rs.getInt("post_id"));
			p.setUser_id(rs.getInt("user_id"));
			p.setBody(rs.getString("body"));
			p.setPost_time(rs.getString("post_time"));
			array.add(p);
		}
		return array; 
	}
	
	public ArrayList<Post> getUserPost(int user_id) throws SQLException {
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement st = conn.prepareStatement("SELECT * FROM post WHERE user_id = ? ORDER BY post_time DESC;");
		st.setInt(1, user_id);
		ResultSet rs = st.executeQuery();
		ArrayList<Post> array = new ArrayList<>();
		while(rs.next()) {
			Post p = new Post();
			p.setPost_id(rs.getInt("post_id"));
			p.setUser_id(rs.getInt("user_id"));
			p.setBody(rs.getString("body"));
			p.setPost_time(rs.getString("post_time"));
			array.add(p);
		}
		return array; 
	}
	
	public void deletePost(int post_id) throws SQLException {
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement st = conn.prepareStatement("DELETE FROM post WHERE post_id = ?;");
		st.setInt(1, post_id);
		st.executeUpdate();
	}
	
	public Post getPost(int post_id) throws SQLException {
		Connection conn = DBConnection.getInstance().getConnection();
		PreparedStatement st = conn.prepareStatement("SELECT * FROM post WHERE post_id = ?;");
		st.setInt(1, post_id);
		ResultSet rs = st.executeQuery();
		Post p = new Post();
		if(rs.next()) {
			p.setPost_id(rs.getInt("post_id"));
			p.setUser_id(rs.getInt("user_id"));
			p.setBody(rs.getString("body"));
			p.setPost_time(rs.getString("post_time"));
		}
		return p; 
	}
	
	public String updatePost(Post post) {
		try {
			Connection conn = DBConnection.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement("UPDATE post SET body = ? WHERE post_id = ?;");
			st.setString(1, post.getBody());
			st.setInt(2, post.getPost_id());
			st.execute();
			return "Post Update Successful.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Post Update Failed.";
		}
	}
}
