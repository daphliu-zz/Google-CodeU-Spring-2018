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
<title><%=conversation.getTitle()%></title>
<link rel="stylesheet" href="/css/main.css" type="text/css">
<link rel="stylesheet" href="/css/iframe.css">

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
		var chatDiv = document.querySelector('.chat');
		chatDiv.scrollTop = chatDiv.scrollHeight;
	};
</script>

</head>

<body onload="scrollChat()">

	<nav>
		<a id="navTitle" href="/">007 Chat App</a> <a href="/conversations">Conversations</a>
		<%
		  if (request.getSession().getAttribute("user") != null) {
		%>
		<a>Hello <%=request.getSession().getAttribute("user")%>!</a>
		<%
		  } else {
		%>
		<a href="/login">Login</a> <a href="/register">Register</a>
		<%
		  }
		%>
		<a href="/about.jsp">About</a>
    <% if(request.getSession().getAttribute("is_admin") != null){ %>
      <a href="/adminStats">Admin Stats</a>
      <a href="/testdata">Load Test Data</a>
    <% } %>
	</nav>

	<div id="container">
		<h1><%=conversation.getTitle()%>
			<a href="" style="float: right">&#8635;</a>
		</h1>
		<hr />

    <div class="tab">
      <button class="tablinks" onclick="openTab(event, 'biggerChat')" id = "defaultOpen">Chat</button>
      <button class="tablinks" onclick="openTab(event, 'rmMember')">Remove Members</button>
      <button class="tablinks" onclick="openTab(event, 'addMember')">Add Members</button>
    </div>
    <hr/>

      <div id="rmMember" class="tabcontent">
      <h3>Members</h3>
      <p>These are the members in the Conversation.</p>
          <%
           Set<UUID> userUUIDs = conversation.getMembers();
            for (UUID userID : userUUIDs) {
              String uuidToString = userID.toString();
              String author = "";
              try{
                author = UserStore.getInstance().getUserFromPD(uuidToString).getName();
              }catch(Exception e){
                throw new Error(e);
              }
          %>
          <div id="oneUser"><%= author%>
            <form action="/modMembers" method="POST" onsubmit="return isValidForm()">
            <%  if (!userID.equals(conversation.getOwnerId())){%>
            <span id="removeBtn"><button type="submit" value= "<%= userID%>" name="remove" id= "remove">Remove <%= author%></button></span>
            <%  } %>
            <input type = "hidden" name="chatTitle" value= "<%= conversation.getTitle() %>"/>
            </form>
          </div>
          <%
            }
          %>
      </div>

      <div id = "addMember"  class = "tabcontent">
        <h2> Discover: </h2>
        <h3> Add new members: </h3>
        <%
          List<User> users = UserStore.getInstance().getAllUsers();
          for (User user : users) {
              String userN = user.getName();
              UUID userUUID = user.getId();
              if (!conversation.isMember(user.getId())){
        %>
              <p><%= userN %></p>
              <form action="/modMembers" method="POST" onsubmit="return isValidForm()">
                <span id="addBtn"><button type="submit" value= "<%= userUUID%>" name="addMbr" id= "add">Add <%= userN%></button></span>
                <input type = "hidden" name="chatTitle" value= "<%= conversation.getTitle() %>"/>
              </form>
          <%
              }
            }
          %>
      </div>

      <div id = "biggerChat"  class = "tabcontent">
        <div class="chat">
    			<div class="msglist" id="words">
    				<% for (Message message : messages) {
    					     String author = UserStore.getInstance().getUser(message.getAuthorId()).getName();
    				       if (!author.equals(request.getSession().getAttribute("user"))) {
    				%>
    				    <div class="notusertalk">
    					     <span> <strong><%=author%>:</strong> <%=message.getContent()%>	</span>
    				    </div>
    				<%
    				  } else {
    				%>
    				    <div class="usertalk">
    					     <span> <strong>You:</strong> <%=message.getContent()%>	</span>
    				    </div>
    				<%
    				    }
    				  }
    				%>
    			</div>
    		</div>
      	<hr />
      		<input type="file" id="btn_file" accept="image/*"
      			onchange="setFunction('InsertIMG')" style="display: none"> <img
      			src="" id="output">
      		<script src="/js/TextEditor.js"></script>
      		<%
      		  if (request.getSession().getAttribute("user") != null) {
      		%>

      		<form action="/chat/<%=conversation.getTitle()%>" id="form"
      			method="POST" style="margin-bottom:200px">
      			<p>
      				<button class="editor-button" type="button" id="bBtn"
      					style="font-weight: bold" onclick="setFunction('Bold');" />
      				B
      				</button>
      				<button class="editor-button" type="button" id="bBtn"
      					style="font-weight: bold" onclick="setFunction('Italic');" />
      				I
      				</button>
      				<button class="editor-button" type="button" id="bBtn"
      					style="font-weight: bold" onclick="setFunction('Underline');" />
      				U
      				</button>
      			</p>
      			<p>
      				<iframe id="editor" width="700px" height="60px"
      					style="border: 0px; marginheight: 2px; marginwidth: 2px"></iframe>

      				<input type="hidden" id="inHTML" name="message" />
      				<button type="button" class="btn" value="Send"
      					onclick="doSubmitForm()">Submit</button>
      				<br/>
      			</p>
      		</form>

      		<script>
      			init('editor');
      		</script>
      		<%
      			} else {
      		%>
      		<p>
      			<a href="/login">Login</a> to send a message.
      		</p>
      		<%
      			}
      		%>
      		<hr />
      </div>
	</div>

  <script>
  //changes between tabs
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
  //error checking to delete specific user
  function isValidForm(){
      return confirm("With this User?");
  }

  // Get the element with id="defaultOpen" and click on it so makes that the active tab
   document.getElementById("defaultOpen").click();
  </script>
</body>
</html>
