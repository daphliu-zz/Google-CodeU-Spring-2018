package codeu.controller;

import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.data.Message;

/** Servlet class responsible for loading test data. */
public class AdminStats extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Set up state for handling the load test data request. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
  }

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
   * Gets the Most Active User based off of the user who has sent the most
   * messages
   */
   String getMostActiveUser(){
     Map <User, Integer> usersToMessages = new HashMap <User, Integer>();
     //go through all conversations & gets number of messages &
     //keeps track of all users number of messages
     //make helper function to check length of content in messages
     //to get wordiest user
     String mostActive = "";
     int maxCount = 0;
     for (int i = 0; i < conversationStore.getAllConversations().size(); i++){
       //get users & keep hashmap??
      Conversation currConvo = conversationStore.getAllConversations().get(i);
      List<Message> messagesinCurrConvo = messageStore.getMessagesInConversation(currConvo.getId());
      for (int j = 0; j < messagesinCurrConvo.size(); j++){
        Message oneMessage = messagesinCurrConvo.get(j);
        //get author &
        //User getUser(UUID id) {
        UUID messageAuthorID = oneMessage.getAuthorId();
        User messageAuthor = userStore.getUser(messageAuthorID);
        if (usersToMessages.containsKey(messageAuthor)){
            int prevValue = usersToMessages.get(messageAuthor);
            usersToMessages.put(messageAuthor, prevValue+1);
            if (prevValue+1>maxCount){
              maxCount = prevValue+1;
              mostActive = messageAuthor.getName();
            }
        } else{
          usersToMessages.put(messageAuthor, 1);
          //assign and check most active user throughout
          if (maxCount<1){
            maxCount = 1;
            mostActive = messageAuthor.getName();
          }
        }
      }
     }
     return mostActive;
   }

  /**
   * This function fires when a user requests the /testdata URL. It simply forwards the request to
   * testdata.jsp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    int numUsers = userStore.numUsers();
    int numConversations = conversationStore.getNumConversations();
    int numMessages = messageStore.getNumMessages();
    String newestUser = "";
    if (userStore.getLastUser()!=null){
      newestUser = userStore.getLastUser().getName();
    }
    String mostActiveUser = getMostActiveUser();
    request.setAttribute("numUsers", numUsers);
    request.setAttribute("numConversations", numConversations);
    request.setAttribute("numMessages", numMessages);
    request.setAttribute("newestUser", newestUser);
    request.setAttribute("mostActiveUser", mostActiveUser);
    request.getRequestDispatcher("/WEB-INF/view/admin.jsp").forward(request, response);
  }

  /**
   * TODO: read from file; store in database; somehow send back to doGet method? or
   *      create a function that clears previous database & loads only file data
   * TODO: make a clear data call
   * This function fires when a user submits the testdata form. It loads file data if the user
   * clicked the confirm button.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String confirmButton = request.getParameter("confirm");

    if (confirmButton != null) {
      userStore.loadTestData();
      conversationStore.loadTestData();
      messageStore.loadTestData();
    }

    response.sendRedirect("/");
  }
}
