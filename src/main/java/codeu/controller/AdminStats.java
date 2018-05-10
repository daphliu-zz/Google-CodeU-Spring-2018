package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  private Conversation currentConversation = null;
  private User currentUser = null;
  private String currentTitle = "";
  /** Set up state for handling the load test data request. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
    users = new ArrayList<>();
    conversations = new ArrayList<>();
    messages = new ArrayList<>();
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

  /** Gets the Most Active User based off of the user who has sent the most messages */
  String getMostActiveUser() {
    Map<User, Integer> usersToMessages = new HashMap<User, Integer>();
    // go through all conversations & gets number of messages &
    // keeps track of all users number of messages
    // make helper function to check length of content in messages
    // to get wordiest user
    String mostActive = "";
    int maxCount = 0;
    for (int i = 0; i < conversationStore.getAllConversations().size(); i++) {
      // get users & keep hashmap??
      Conversation currConvo = conversationStore.getAllConversations().get(i);
      List<Message> messagesinCurrConvo = messageStore.getMessagesInConversation(currConvo.getId());
      for (int j = 0; j < messagesinCurrConvo.size(); j++) {
        Message oneMessage = messagesinCurrConvo.get(j);
        UUID messageAuthorID = oneMessage.getAuthorId();
        User messageAuthor = userStore.getUser(messageAuthorID);
        if (usersToMessages.containsKey(messageAuthor)) {
          int prevValue = usersToMessages.get(messageAuthor);
          usersToMessages.put(messageAuthor, prevValue + 1);
          if (prevValue + 1 > maxCount) {
            maxCount = prevValue + 1;
            mostActive = messageAuthor.getName();
          }
        } else {
          usersToMessages.put(messageAuthor, 1);
          // assign and check most active user throughout
          if (maxCount < 1) {
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
    if (userStore.getLastUser() != null) {
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

  /*Takes out capital words letters */
  boolean checkIfAllCapital(String firstWord) {
    for (int i = 0; i < firstWord.length(); i++) {
      if (!Character.isLetter(firstWord.charAt(i)) || !Character.isUpperCase(firstWord.charAt(i))) {
        return false;
      }
    }
    if (firstWord.length() < 2) return false;
    // Then is a title/scene/character/
    return true;
  }

  /*puts user into PersistentDataStore & creates new user if needed as well as
  * updates currentUser*/
  void appendUser(String userName) {
    if (!userStore.isUserRegistered(userName)) {
      User user = new User(UUID.randomUUID(), userName, "password", Instant.now());
      userStore.addUser(user);
      users.add(user);
      currentUser = user;
    } else {
        User foundUser = userStore.getUser(userName);
        currentUser = foundUser;
    }
  }

  /*Puts a message into PersistentDataStore*/
  void appendMessage(String line) {
    Conversation conversation = currentConversation;
    User author = currentUser;
    String content = line;
    Message message =
        new Message(
            UUID.randomUUID(), conversation.getId(), author.getId(), content, Instant.now());
    messageStore.addMessage(message);
    messages.add(message);
  }

  /* Saves the character's message to the character before switching to a new user*/
  void changeToNewUser(String charactersWord, String newUser) {
    if (currentUser != null) {
      if (!charactersWord.equals("")) {
        appendMessage(charactersWord);
      }
      appendUser(newUser);
    } else {
      appendUser(newUser);
    }
  }
  boolean foundWordNarratorDictates(String firstWord, String charactersWord){
    boolean didFind = true;
    switch (firstWord) {
      //these are phrases that the NARRATOR dictates
      case ("**Exit"):
        {
          changeToNewUser(charactersWord, "NARRATOR");
        }
        break;
      case ("Enter"):
        {
          changeToNewUser(charactersWord, "NARRATOR");
        }
        break;
      case ("**Exeunt"):
        {
          changeToNewUser(charactersWord, "NARRATOR");
        }
        break;
        case ("Re-enter"):
          {
            changeToNewUser(charactersWord, "NARRATOR");
          }
          break;
      default:
        { // append user's message if character's words continue onto next lines
          didFind = false;
        }
        break;
      }
      return didFind;
  }

  /**
   * TODO: read from file; store in database; somehow send back to doGet method? or create a
   * function that clears previous database & loads only file data This function fires when a user
   * submits the testdata form. It loads file data if the user clicked the confirm button.
   */
  void readFile(BufferedReader bufferedReader) throws Exception {
    //TODO: if NARRATOR Interrupts use last user to continue text conversation if no user
    String line = null;
    boolean addToLine = false;
    String charactersWord = "";
    String savedLine = null;
    // use the readLine method of the BufferedReader to read one line at a time.
    // the readLine method returns null when there is nothing else to read.
    while ((line = bufferedReader.readLine()) != null) {
      savedLine = line;
      boolean addNewUser = false;
      String firstWord = line;
      if (firstWord.contains("ACT")) {
        firstWord = "ACT";
      } else if (firstWord.contains(" ")) {
        firstWord = firstWord.substring(0, firstWord.indexOf(" "));
      }
      if (checkIfAllCapital(firstWord)) {
        addToLine = true;
        String userName = line;
        switch (firstWord) {
          case "ACT":
            // new CONVERSATION
            {
              changeToNewUser(charactersWord, "NARRATOR");
              charactersWord = "";
              User user = userStore.getUser("NARRATOR");
              String title = currentTitle+"_"+ line;
              Conversation conversation =
                  new Conversation(UUID.randomUUID(), user.getId(), title, Instant.now());
              conversationStore.addConversation(conversation);
              conversations.add(conversation);
              currentConversation = conversation; // secondtime may not have?
            }
            break;
          case "SCENE":
            { //Narrator dictates that a new scene has started
              changeToNewUser(charactersWord, "NARRATOR");
              charactersWord = line;
            }
            break;
          default:
            {
              addNewUser = true;
            }
            break;
        }
        if (addNewUser) {
          changeToNewUser(charactersWord, userName);
          charactersWord = "";
        }
      } else {
          if (foundWordNarratorDictates(firstWord, charactersWord)){
              charactersWord = line;
          } else{
            charactersWord = charactersWord + " " + line;
          }
      }
    }
    appendMessage(savedLine);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String selectedValue = request.getParameter("titles");
    String confirmButton = request.getParameter("confirm");

    if (confirmButton != null) {
      String specificFile = "";
      if (selectedValue.equals("romandjul")) {
        specificFile = "romandjul.txt";
        currentTitle = "R&J";
      }
      if (selectedValue.equals("julcaesar")){
        specificFile = "julcaesar.txt";
        currentTitle="JulC";
      }
      if (selectedValue.equals("midsumDream")){
        specificFile = "midsumDream.txt";
        currentTitle="MidsumDream";
      }
      if (selectedValue.equals("tempest")){
        specificFile = "tempest.txt";
        currentTitle="Tempest";
      }
        try {
          String findFile = "../../src/main/java/codeu/controller/"+specificFile;
          BufferedReader bufferedReader =
              new BufferedReader(
                  new FileReader(findFile));
          System.out.println("opened this file");
          System.out.println(findFile);
          readFile(bufferedReader);
          bufferedReader.close();
        } catch (Exception e) {
          //e.printStackTrace(System.out);
          System.out.println("DIDNT OPEN");
        }

    }
    response.sendRedirect("/");
  }
}
