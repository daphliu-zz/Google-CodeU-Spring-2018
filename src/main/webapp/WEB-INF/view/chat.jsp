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
<%@ page import="java.util.List"%>
<%@ page import="codeu.model.data.Conversation"%>
<%@ page import="codeu.model.data.Message"%>
<%@ page import="codeu.model.store.basic.UserStore"%>
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
		<a id="navTitle" href="/">CodeU Chat App</a> <a href="/conversations">Conversations</a>
		<%
		  if (request.getSession().getAttribute("user") != null) {
		%>
		<a>Hello <%=request.getSession().getAttribute("user")%>!
		</a>
		<%
		  } else {
		%>
		<a href="/login">Login</a> <a href="/register">Register</a>
		<%
		  }
		%>
		<a href="/about.jsp">About</a>
	</nav>

	<div id="container">

		<h1><%=conversation.getTitle()%>
			<a href="" style="float: right">&#8635;</a>
		</h1>

		<hr />

		<div class="chat">
			<div class="msglist" id="words">

				<%
				  for (Message message : messages) {
								String author = UserStore.getInstance().getUser(message.getAuthorId()).getName();
				%>

				<%
				  if (!author.equals(request.getSession().getAttribute("user"))) {
				%>

				<div class="notusertalk">

					<span> <strong><%=author%>:</strong> <%=message.getContent()%>
					</span>

				</div>

				<%
				  } else {
				%>

				<div class="usertalk">
					<span> <strong>You:</strong> <%=message.getContent()%>
					</span>
				</div>

				<%
				  }
				%>

				<%
				  }
				%>
			</div>

		</div>

		<hr />
		<input type="file" id="btn_file" accept="image/*"
			onchange="setFunction('InsertIMG')" style="display: none"> <img
			src="" id="output">
		<script src="http://localhost:8080/js/TextEditor.js"></script>
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
				<br />
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



</body>
</html>
