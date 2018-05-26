<!DOCTYPE html>
<html>
<head>
  <title>Admin View</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">007 Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a href="/logout">Logout</a>
    <% } else{ %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
    <% if(request.getSession().getAttribute("is_admin") != null){ %>
      <a href="/adminStats">Admin Stats</a>
      <a href="/testdata">Load Test Data</a>
    <% } %>
  </nav>

  <div id="container">
    <h1>Site Statistics</h1>
    <p>This will display data for admin to view.</p>
    <form action="/adminStats" method="POST">

      <p>Users: <%= request.getAttribute("numUsers")%></p>
      <p>Conversations: <%= request.getAttribute("numConversations")%></p>
      <p>Messages: <%= request.getAttribute("numMessages")%></p>
      <p>Newest User: <%= request.getAttribute("newestUser")%></p>
      <p>Most Active User: <%= request.getAttribute("mostActiveUser")%></p>

    </form>

  </div>

    <div id="container">
      <h1> Import Data</h1>
      <%-- Allow for user upload data--%>
      <p></p>
      <form action="/adminStats" method="POST">
      <select name = "titles">
          <option value="midsumDream">A Midsummer Night's Dream</option>
          <option value="romandjul">Romeo And Juliet</option>
          <option value="julcaesar">Julius Caesar</option>
          <option value="tempest">Tempest</option>
      </select>
      <button type="submit" value="confirm" name="confirm">Confirm</button>
    </div>

    </div>
    <div id="container">
      <h1> Manage Admins</h1>
      <%-- Allow for admins to promote or demote an user to/from admin status--%>
      <form action="/adminStats" method="POST">
        <label for="username">Username: </label>
        <input type="text" name="username" id="username">
        <br/><br/>
        <button type="submit" value="promote" name="change_admin_status">Promote</button>
        <button type="submit" value="demote" name="change_admin_status">Demote</button>
      </form>

      <% if(request.getAttribute("error") != null){ %>
        <h2 style="color:red"><%= request.getAttribute("error") %></h2>
      <% } %>

      <% if(request.getAttribute("success") != null){ %>
        <h2 style="color:green"><%= request.getAttribute("success") %></h2>
      <% } %>
    </div>
</body>
</html>
