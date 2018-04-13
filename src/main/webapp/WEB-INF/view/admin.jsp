<!DOCTYPE html>
<html>
<head>
  <title>Admin View</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
  </nav>

  <% if(request.getAttribute("error") != null){ %>
    <h2 style="color:red"><%= request.getAttribute("error") %></h2>
  <% } %>

  <% if(request.getAttribute("success") != null){ %>
    <h2 style="color:red"><%= request.getAttribute("error") %></h2>
  <% } %>

  <div id="container">
    <h1>Site Statistics</h1>
    <p>This will display data for admin to view.</p>
    <form action="/adminStats" method="POST">

      <p>Users: <%= request.getAttribute("numUsers")%></p>
      <p>Conversations: <%= request.getAttribute("numConversations")%></p>
      <p>Messages: <%= request.getAttribute("numMessages")%></p>
      <p>Newest User: <%= request.getAttribute("newestUser")%></p>

    </form>
  
  </div>
    <div id="container">
      <h1> Import Data</h1>
      <%-- Allow for user upload data--%>
      <label for = "file"> Choose file to upload</label>
      <input name="myFile" type="file">
      <button type="submit" value="confirm" name="confirm">Submit</button>
      <%-- TODO: have Submit button reload page to show data for file uploaded--%>
    </div>

    </div>
    <div id="container">
      <h1> Manage Admins</h1>
      <%-- Allow for admins to promote or demote an user to/from admin status--%>
      <form action="/adminStats" method="POST">
        <label for="username">Username: </label>
        <input type="text" name="username" id="username">
        <br/><br/>
        <button type="submit" value="promote" name="promote_user">Promote</button>
        <button type="submit" value="demote" name="promote_user">Demote</button>
      </form>
    </div>
</body>
</html>
