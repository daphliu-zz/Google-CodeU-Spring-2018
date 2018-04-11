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

  <div id="container">
    <h1>Site Statistics</h1>
    <p>This will display data for admin to view.</p>
    <form action="/adminStats" method="POST">

      <p>Users: <%= request.getAttribute("numUsers")%></p>
      <p>Conversations: <%= request.getAttribute("numConversations")%></p>
      <p>Messages: <%= request.getAttribute("numMessages")%></p>
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
</body>
</html>
