package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyMembersServlet extends HttpServlet {
  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method
   * for use by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * Set up state for handling conversation-related requests. This method is only called when
   * running in a server, not when running in a test.
   */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

      }

  /**
   * This function fires when a user submits the add/remove member on the chat page. It gets
   *  the conversation title from the form, and the user signed in from the session.
   *  It removes or adds a new member to the conversaion and then
   * redirects back to the given conversation.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // gets current conversation & updates according to if the user clicked add or remove
    String conversationTitle = request.getParameter("chatTitle");
    Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);

    String sV = request.getParameter("remove");
    String addM = request.getParameter("addMbr");

    if (addM != null) { // means addM was pushed
      try {
        // loads conversation & adds user to members list
        conversationStore.removeConversationFromInStoreList(conversation);
        conversationStore.addMemberinPD(conversation.getId(), UUID.fromString(addM));
        Conversation newConvo = conversationStore.getConversationFromPD(conversation.getId());
        // still needs to update to current conversationStore since only added to persistent
        // database
        conversationStore.addConversationToInStoreList(newConvo);
      } catch (Exception e) {

      }
    }
    if (sV != null) { // means remove btn was pushed
      try {
        // load conversation & remove user from members list
        conversationStore.removeConversationFromInStoreList(conversation);
        conversationStore.removeMemberinPD(conversation.getId(), UUID.fromString(sV));
        Conversation newConvo = conversationStore.getConversationFromPD(conversation.getId());
        // still needs to update to current conversationStore since only added to persistent
        // database
        conversationStore.addConversationToInStoreList(newConvo);
      } catch (Exception f) {
      }
    }

    response.sendRedirect("/chat/" + conversationTitle);
  }
}
