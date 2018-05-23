<%--
  Copyright 2017 Google Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.UUID" %>
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">

  <style>
    #chat {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

  <script>
    // scroll the chat div to the bottom
    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    };
  </script>

</head>
<body onload="scrollChat()">

  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
      <% if (request.getSession().getAttribute("user") != null) { %>
    <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else { %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
  </nav>

  <div id="container">

    <h1><%= conversation.getTitle() %>
      <a href="" style="float: right">&#8635;</a></h1>

    <hr/>
    <div class="tab">
      <button class="tablinks" onclick="openTab(event, 'biggerChat')" id = "defaultOpen">Chat</button>
      <button class="tablinks" onclick="openTab(event, 'addMember')">Members</button>
    </div>


    <div id="addMember" class="tabcontent">
    <h3>Members</h3>
    <p>These are the members in the Conversation.</p>
      <%

       Set<UUID> userUUIDs = conversation.getMembers();
        for (UUID user : userUUIDs) {
          String uuidToString = user.toString();
          String author = "";
          try{
            author = UserStore.getInstance().getUserFromPD(uuidToString).getName();
          }catch(Exception e){

          }
      %>
        <div id="oneUser"><%= author%>
      <form action="/modMembers" method="POST" onsubmit="return isValidForm()">

        <span id="removeBtn"><button type="submit" value= "<%= author%>" name="remove" id= "remove">Remove <%= author%></button></span>
        <input type = "hidden" name="chatTitle" value= "<%= conversation.getTitle() %>"/>
      </form>
      </div>
      <%

        }
      %>
  </div>

<div id = "biggerChat"  class = "tabcontent">
    <div id="chat">
      <ul>
    <%
      for (Message message : messages) {
        String author = UserStore.getInstance()
          .getUser(message.getAuthorId()).getName();
    %>
      <li><strong><%= author %>:</strong> <%= message.getContent() %></li>
    <%
      }
    %>
      </ul>
    </div>

    <hr/>

    <% if (request.getSession().getAttribute("user") != null) { %>
    <form action="/chat/<%= conversation.getTitle() %>" method="POST">
        <input type="text" name="message">
        <br/>
        <button type="submit">Send</button>
    </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>
</div>
  </div>
  <script>
  function openTab(evt, tabName) {
      var i, tabcontent, tablinks;
      tabcontent = document.getElementsByClassName("tabcontent");
      for (i = 0; i < tabcontent.length; i++) {
          tabcontent[i].style.display = "none";
      }
      tablinks = document.getElementsByClassName("tablinks");
      for (i = 0; i < tablinks.length; i++) {
          tablinks[i].className = tablinks[i].className.replace(" active", "");
      }
      document.getElementById(tabName).style.display = "block";
      evt.currentTarget.className += " active";
  }
  function isValidForm(){
      //console.log(document.getElementById("remove").textContent);

      return confirm("Delete this user?");
  }

  // Get the element with id="defaultOpen" and click on it
   document.getElementById("defaultOpen").click();
  </script>
</body>
</html>
