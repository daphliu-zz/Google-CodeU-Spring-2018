package codeu.controller;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.*;

public class ModifyMembersServlet extends HttpServlet {

  @Override
  public void init() throws ServletException {
    super.init();
  }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response)
     throws IOException, ServletException {
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException
  {
    // Getting request information without the hostname.

        String uri = request.getRequestURI();
        System.out.println(uri);
        System.out.println("HELLO");
        String conversationTitle= request.getParameter("chatTitle");
        System.out.println(conversationTitle);
        String sV = request.getParameter("remove");
        //load conversation & remove user from list
        System.out.println(sV);
        response.sendRedirect("/chat/" + conversationTitle);
  }

}
