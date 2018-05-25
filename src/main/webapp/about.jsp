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
<!DOCTYPE html>
<html>
<head>
  <title>CodeU Chat App</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getParameter("offline") != null){ %>
    <% } else if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
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
    <div
      style="width:100%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>About Team 007's CodeU Chat App</h1>
      <p>
       A brief introduction to the members of Team 007 and the use of this chat app.
      </p>

      <div class="members">
        <div>
          <img src="memberPhotos/daphne.jpg" alt="Member: Daphne Liu" style="width:300px; height:320px;">
          <p id="memberInfo">
            <strong>Daphne Liu</strong>  </br>
            <strong>Education:</strong> University of British Columbia, Computer Science</br>
            <strong>Interests:</strong> Mobile, VR, Doctor Who </br>
            <strong>Achievements: </strong>Supported login form to confirm validity of users and redirect
            to sessions Added custom routing and database manipulation in Java for
            administrators Introduced offline support by caching HTML pages and CSS
            with JavaScript using service workers.
         </p>
       </div>

        <div>
          <img src="memberPhotos/ken.jpg" alt="Member: Ken Wang" style="width:300px; height:320px;">
            <p id="memberInfo">
              <strong>Ken Wang</strong> </br>
              <strong>Education:</strong>University of Utah, Computer Science</br>
              <strong>Interests:</strong> Entertaining Arts, Game engineering, Web</br>
              <strong>Achievements: </strong>Created Register related page and the backend servlet
              for new users. Reinvented the wheel of a Rich Text Editor for the purpose of a
              better looking and functional Chat page. Beautified the Chat page.

           </p>
          </div>
        </div>


        <div class="members">
        <div>
          <img src="memberPhotos/blank.png" alt="Member: Natasha Sarkar" style="width:300px; height:320px;">
            <p id="memberInfo">
              <strong>Natasha Sarkar</strong>  </br>
              <strong>Education:</strong> UCLA, Computer Science & Computer Engineering</br>
              <strong>Interests:</strong>  </br>
              <strong>Achievements: </strong> Styling text in messages
           </p>
         </div>
        <div>
           <img src="memberPhotos/sonia.jpg" alt="Member: Sonia Velasco" style="width:300px; height:320px;">
          <p id="memberInfo">
            <strong>Sonia Velasco</strong>  </br>
            <strong>Education:</strong>Stanford University, Computer Science</br>
            <strong>Interests:</strong> BTS </br>
            <strong>Achievements: </strong> Implemented access to site statistics;
            Allowed access control to conversations with users being able to delete/add
            members to a conversation
         </p>
       </div>
      </div>

        <div class="members">
        <div>
          <img src="memberPhotos/desiye.jpeg" alt="Member: Desiye Collier" style="width:300px; height:320px;">
            <p id="memberInfo">
              <strong>Desiye Collier</strong>  </br>
              <strong>Project Advisor: </strong>Google Software Engineer on the Adwords API team
           </p>
         </div>
      </div>


    </div>

      <ul>
        <li><strong>Usage: </strong> Feel free to create new accounts and add users
        to conversations. In order to view our stats page, simply have "admin"
         as the username with no password. </li>
        <li><strong>Features:</strong> The user can edit their text using our
      text editor. One can also view stats on usage. The user can control who views their
    conversations.  </li>
      </ul>


  </div>
</body>
</html>
