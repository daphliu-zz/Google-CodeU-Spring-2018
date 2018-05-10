// Java Program to illustrate reading from FileReader
// using BufferedReader

package codeu.controller;
import java.io.*;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.*;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

public class ParseTextHelper
{
  private List<User> users;
  private List<Conversation> conversations;
  private List<Message> messages;
  private Conversation currentConversation = null;
  private User currentUser = null;
  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /**File imported by User selection**/
  //String fileName;
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
  public ParseTextHelper(BufferedReader fileName) throws Exception{
    setUserStore(UserStore.getInstance());
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    //this.fileName = fileName;
    users = new ArrayList<>();
    conversations = new ArrayList<>();
    messages = new ArrayList<>();
    readFile(fileName);
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

  // puts user into PersistentDataStore & creates new user if needed as well as
  // updates currentUser
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

  // Puts a message into PersistentDataStore
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

  // Saves the character's message to the character before switching to a new user
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

  /**
   * TODO: read from file; store in database; somehow send back to doGet method? or create a
   * function that clears previous database & loads only file data This function fires when a user
   * submits the testdata form. It loads file data if the user clicked the confirm button.
   */
  void readFile(BufferedReader bufferedReader) throws Exception {
    String line = null;
    boolean addToLine = false;
    String charactersWord = "";
    String savedLine = null;
    // use the readLine method of the BufferedReader to read one line at a time.
    // the readLine method returns null when there is nothing else to read.
    while ((line = bufferedReader.readLine()) != null) {
      savedLine = line;
      boolean addNewUser = false;
      // TODO: conversation @ new SCENE
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
              String title = line;
              Conversation conversation =
                  new Conversation(UUID.randomUUID(), user.getId(), title, Instant.now());
              conversationStore.addConversation(conversation);
              conversations.add(conversation);
              currentConversation = conversation; // secondtime may not have?
            }
            break;
          case "SCENE":
            {
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
        switch (firstWord) {
          case ("**Exit"):
            {
              changeToNewUser(charactersWord, "NARRATOR");
              charactersWord = line;
            }
            break;
          case ("Enter"):
            {
              changeToNewUser(charactersWord, "NARRATOR");
              charactersWord = line;
            }
            break;
          case ("**Exeunt"):
            {
              changeToNewUser(charactersWord, "NARRATOR");
              charactersWord = line;
            }
            break;
          default:
            { // append user's message
              charactersWord = charactersWord + " " + line;
            }
            break;
        }
      }
    }
    appendMessage(savedLine);
    // TESTING ARRAY: System.out.println("this is user size: ");
    // System.out.print(users.size());
    // for (int i = 0; i < users.size(); i++){
    //   User currUser = users.get(i);
    //   System.out.println(currUser.getName());
    // } //only prints out first time since starts off empty
    //bufferedReader.close();
  }
}
