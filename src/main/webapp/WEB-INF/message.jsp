<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="model.User"%>
<%@ page import="model.Message"%>
<%@ page import="dao.UserDAO"%>
<%
if (session == null || session.getAttribute("user_id") == null) {
	response.sendRedirect("login");
}
%>
	
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Message | Social</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<link rel="shortcut icon" href="image/logo.png">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css">
<style>
.user-entry {
    background-color: #f8f9fa; /* Light grey background */
    margin-bottom: 5px; /* Space between entries */
    padding: 5px; /* Padding inside each entry */
    border-radius: 3px; /* Rounded corners for each entry */
}
	.container {
	    margin-top: 120px;
	}
	
	@media (max-width:800px){
		.container {
	    	margin-top: 240px !important;
		}
	}
	
	#newMessageBtn {
		display: block;
		position: fixed;
		bottom: 20px;
		right: 30px;
		z-index: 99;
		font-size: 18px;
		border: none;
		outline: none;
		background-color: #1fa3f4;
		color: white;
		cursor: pointer;
		padding: 15px;
		border-radius: 100%;
		width: 60px;
    	height: 60px;
	}
	.no-results {
    color: #666; /* Gray text color */
    text-align: center; /* Center the text */
    padding: 10px; /* Add some padding */
}
	
	#newMessageBtn:hover {
		background-color: #0373b7;
	}
</style>

<script>
$(document).ready(function() {
    var userId = '<%= session.getAttribute("user_id") %>'; // Get user ID from session

    $('#searchInput').on('input', function() {
        var query = $(this).val().trim();
        if (query.length === 0) {
            $('#searchResults').empty();
            showSearchHistory(); // Show search history when input is empty
            return;
        }
        hideSearchHistory(); // Hide search history when typing
        fetchSearchResults(query);
    });

    $('#searchInput').on('focus', function() {
        if ($(this).val().trim().length === 0) {
            showSearchHistory();
        }
    });

    $('#searchInput').on('blur', function() {
        setTimeout(function() { // Timeout to allow click event to process
            hideSearchHistory();
        }, 200);
    });

    function fetchSearchResults(query) {
        $.ajax({
            url: '${pageContext.request.contextPath}/FriendSearchServlet',
            type: 'GET',
            data: {query: query},
            success: function(response) {
                $('#searchResults').empty();
                if (response.length === 0) {
                    $('#searchResults').append('<div class="no-results">No results found.</div>');
                } else {
                    response.forEach(function(user) {
                        var userDiv = $('<div class="user-entry">' + user.firstName + ' ' + user.lastName + '</div>');
                        userDiv.on('click', function() {
                            saveSearchToCookies(user.firstName + ' ' + user.lastName);
                        });
                        $('#searchResults').append(userDiv);
                    });
                }
                saveSearchToCookies(query);
            },
            error: function(xhr, status, error) {
                console.error("An error occurred: " + status + " " + error);
            }
        });
    }

    function showSearchHistory() {
        var history = getCookie("searchHistory_" + userId);
        if (history) {
            var searches = history.split(',');
            $('#searchHistory').empty();
            searches.forEach(function(search) {
                var searchDiv = $('<div class="user-entry">' + search + '</div>');
                searchDiv.on('click', function() {
                    $('#searchInput').val(search);
                    fetchSearchResults(search);
                });
                $('#searchHistory').append(searchDiv);
            });
            $('#searchHistory').show();
        }
    }

    function hideSearchHistory() {
        $('#searchHistory').hide();
    }

    function saveSearchToCookies(searchTerm) {
        var now = new Date();
        now.setTime(now.getTime() + 24 *3600* 1000); // 1 day expiration
        var searches = getCookie("searchHistory_" + userId) ? getCookie("searchHistory_" + userId) + ',' + searchTerm : searchTerm;
        document.cookie = "searchHistory_" + userId + "=" + encodeURIComponent(searches) + "; expires=" + now.toUTCString() + "; path=/";
    }

    function getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return decodeURIComponent(c.substring(nameEQ.length,c.length));
        }
        return null;
    }
});
</script>
</head>
<body class="bg-dark">


	<%@include file="header.jsp" %>

	<main role="main">

	 <div class="container">
            <input type="text" id="searchInput" placeholder="Search friends..." autocomplete="off">
            <div id="searchResults"></div>
            <div id="searchHistory" class="search-history"></div>
           
        </div>
		
		<%
			ArrayList<Message> messages = (ArrayList<Message>) request.getAttribute("messages");
			
			if(messages.size() == 0){
				%><h4 style="text-align: center; color: #ffffff;">No Messages.</h4><%
			}
			
			for(int i=0; i<messages.size(); i++){
				if(messages.get(i).getFrom_user().equals(session.getAttribute("user_id").toString())){
					%>
						<div class="card mb-3">
						  <div class="card-body">
						  	<div class="row">
						  		<div class="col-2">
							  		<img style="width: 100%; border-radius: 100%;" src="<%= new UserDAO().getUserById(Integer.parseInt(messages.get(i).getTo_user())).getImage() %>" />
							  	</div>
							  	<div class="col-10" style="cursor: pointer;" onclick="window.location.href='view-message?id=<%= messages.get(i).getTo_user() %>'">
								  	<h5 class="card-title"><%= new UserDAO().getUserById(Integer.parseInt(messages.get(i).getTo_user())).getFirst_name() %></h5>
								    <h6 class="card-subtitle mb-2 text-muted"><%= messages.get(i).getMessage() %></h6>
								    <p class="card-text"><%= messages.get(i).getChat_time() %></p>
							  	</div>
							 </div>
							 <div class="row">
								<div class="col-12">
						  			<a href="${pageContext.request.contextPath}/message?delete=<%=messages.get(i).getTo_user() %>" class="card-link" style="float: right;"><i style="font-size: 25px;color:red;" class="far fa-trash-alt"></i></a>
						  		</div>							  
						  	 </div>
							</div>
						</div>
					<%
				} else {
					%>
					<div class="card mb-3">
					  <div class="card-body">
					  	<div class="row">
					  		<div class="col-2">
						  		<img style="width: 100%; border-radius: 100%;" src="<%= new UserDAO().getUserById(Integer.parseInt(messages.get(i).getFrom_user())).getImage() %>" />
						  	</div>
						  	<div class="col-10" style="cursor: pointer;" onclick="window.location.href='view-message?id=<%= messages.get(i).getTo_user() %>'">
							  	<h5 class="card-title"><%= new UserDAO().getUserById(Integer.parseInt(messages.get(i).getFrom_user())).getFirst_name() %></h5>
							    <h6 class="card-subtitle mb-2 text-muted"><%= messages.get(i).getMessage() %></h6>
							    <p class="card-text"><%= messages.get(i).getChat_time() %></p>
						  	</div>
						</div>
						<div class="row">
							<div class="col-12">
						  		<a href="${pageContext.request.contextPath}/message?delete=<%=messages.get(i).getFrom_user() %>" class="card-link" style="float: right;"><i style="font-size: 25px;color:red;" class="far fa-trash-alt"></i></a>
						  	</div>
						</div>
						</div>
					</div>
					<%
				}
			}
		%>
		
		</div>
		
		<button id="newMessageBtn" data-toggle="modal" data-target="#newChatModal">
			<i class="fas fa-plus"></i>
		</button>
		
		<!-- Modal -->
		<div class="modal fade" id="newChatModal" tabindex="-1" role="dialog"
			aria-labelledby="chatModalTitle" aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="chatModalTitle">Friends List</h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body"
						style="overflow-y: scroll; max-height: 350px;">
						
				
						<%
							ArrayList<User> newUsers = (ArrayList<User>) request.getAttribute("newUsers");
							
							for (int i = 0; i < newUsers.size(); i++) {
							%>
		
							<div style="cursor: pointer;  margin: 5px;" class="card"
								onclick="javascript:window.location='view-message?id=<%=newUsers.get(i).getUser_id()%>';">
								<div class="card-body">
									<%=newUsers.get(i).getFirst_name() + " " + newUsers.get(i).getLast_name()%>
									<i style="float: right;" class="fas fa-paper-plane"></i>
								</div>
							</div>
		
							<%
							}
						%>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>
		
	</main>
	  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</body>
</html>