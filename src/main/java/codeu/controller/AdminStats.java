package codeu.controller;

import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import java.io.*;
/** Servlet class responsible for loading test data. */
public class AdminStats extends HttpServlet {

  private List<User> users;
  private List<Conversation> conversations;
  private List<Message> messages;

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


  /*Takes out capital words letters & sees if they match with "Act", "Scene" or character name*/
  boolean checkIfAllCapital(String firstWord){

    for(int i = 0; i < firstWord.length(); i++){
       if(!Character.isLetter(firstWord.charAt(i)) || !Character.isUpperCase(firstWord.charAt(i))){
          //Then is a title/scene/character/
          return false;
      }
    }
    return true;
  }

  /**
   * TODO: read from file; store in database; somehow send back to doGet method? or
   *      create a function that clears previous database & loads only file data
   * TODO: make a clear data call
   * This function fires when a user submits the testdata form. It loads file data if the user
   * clicked the confirm button.
   */
   void readFile(BufferedReader bufferedReader) throws Exception{
     String line = null;
     boolean addToLine = false;
     boolean alreadySawSpecialChar = false;
     int count = 0;
     // use the readLine method of the BufferedReader to read one line at a time.
     // the readLine method returns null when there is nothing else to read.
     while ((line = bufferedReader.readLine()) != null)
     {
       System.out.println(line);
       boolean addNewUser = false;
       //TODO: conversation @ new SCENE
       String firstWord = line;
       if(firstWord.contains(" ")){
         firstWord= firstWord.substring(0, firstWord.indexOf(" "));
         //System.out.println(firstWord);
       }
       System.out.println("this is first word: ");
       System.out.println(firstWord);
       if (checkIfAllCapital(firstWord)){
         //TODO: check what kinda capital word it is
           addToLine = true;
           String userName = line;
           switch(firstWord){
             case "ACT":
             //new CONVERSATION
             {if (userStore.getUser("NARRATOR")==null){
               //make new user
               User user = new User(UUID.randomUUID(), "NARRATOR", "password", Instant.now());
               PersistentStorageAgent.getInstance().writeThrough(user);
               users.add(user);
             }
             User user = userStore.getUser("NARRATOR"); //TODO: or create if none
             String title = line;
             Conversation conversation =
                 new Conversation(UUID.randomUUID(), user.getId(), title, Instant.now());
             PersistentStorageAgent.getInstance().writeThrough(conversation);
             conversations.add(conversation);}
             break;
             case "SCENE":
             {userName = "NARRATOR";
             addNewUser = true;}
             break;
             //USER = narrator
             default://is new USER  or old????
             {addNewUser = true;}
             break;
           }
           if (addNewUser){
             System.out.println("in nbool addnewUs");
             System.out.println(userName);
             if (userStore.getUser(userName)==null){
               //checkSpecialCharacter(firstWord);
               User user = new User(UUID.randomUUID(), userName, "password", Instant.now());
               PersistentStorageAgent.getInstance().writeThrough(user);
               users.add(user);
             }
           }
           //create new user
           //TODO: True == new USER/ else == new Conversation ///new message = new scene==new USER NARRATOR
           //IF EXIT &&& NO NEW CHARACTER USE OLD CHARACTER
           alreadySawSpecialChar = true;

         //go to next Line until and append until NEW Special Character
         //keep adding until NEW special Character
       } else{
         switch (firstWord){ //take along the rest of the line
           case "**Exit": break;
           case "**Enter": break;
           case "**Exeunt": break;
           default: break;
         }
         //append
         if (alreadySawSpecialChar){
           //keep appending
         } else{
           //start new message
         }
       }
     }
     bufferedReader.close();
   }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException{

    String confirmButton = request.getParameter("confirm");

    if (confirmButton != null) {
      try{

      //  FileReader fileN = new FileReader("testing.txt");
      //   File file = new File("../../src/main/java/codeu/controller/testing.txt");
      //   if (!file.exists()){
      //   System.err.println(file.getName() + " not found. Full path: " + file.getAbsolutePath());
      // } else{
      //
      // }
      //
      BufferedReader bufferedReader = new BufferedReader(new FileReader("../../src/main/java/codeu/controller/testing.txt"));
      // //   String line = null;
      // ParseTextHelper addRomeo = new ParseTextHelper(bufferedReader);
      // bufferedReader.close();


      users = new ArrayList<>();
      conversations = new ArrayList<>();
      messages = new ArrayList<>();
      readFile(bufferedReader);
      for (int i = 0; i < users.size(); i++){
        User currUser = users.get(i);
        System.out.println(currUser.getName());
      }
      } catch(Exception e){
        System.out.println("DIDNT OPEN");
      }
      // userStore.loadTestData();
      // conversationStore.loadTestData();
      // messageStore.loadTestData();
    }

    response.sendRedirect("/");
  }
}
