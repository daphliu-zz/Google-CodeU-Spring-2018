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
      <%-- TODO: have Submit button reload page to show data for file uploaded--%>
    </div>
</body>
</html>
