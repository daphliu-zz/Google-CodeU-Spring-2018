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

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /**File imported by User selection**/
  //String fileName;
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

  private void ParseTextHelper1(String fileName) throws Exception{
    setUserStore(UserStore.getInstance());
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
  //  this.fileName = fileName;
    users = new ArrayList<>();
    conversations = new ArrayList<>();
    messages = new ArrayList<>();
//    readFile(this.fileName);
  }
  void readFile(BufferedReader bufferedReader) throws Exception{
    String line = null;
    boolean addToLine = false;
    boolean alreadySawSpecialChar = false;
    // use the readLine method of the BufferedReader to read one line at a time.
    // the readLine method returns null when there is nothing else to read.
    while ((line = bufferedReader.readLine()) != null)
    {
      System.out.println(line);
      boolean addNewUser = false;
      //TODO: conversation @ new SCENE
      String[] currLine = line.split("\\s");
      String firstWord = currLine[0];
    //  System.out.println(firstWord);
      if (checkIfAllCapital(firstWord)){
        //TODO: check what kinda capital word it is
          addToLine = true;
          String userName = line;
          switch(firstWord){
            case "ACT":
            //new CONVERSATION
            if (userStore.getUser("NARRATOR")==null){
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
            conversations.add(conversation);
            break;
            case "SCENE":
            userName = "NARRATOR";
            addNewUser = true;
            break;
            //USER = narrator
            default://is new USER  or old????
            addNewUser = true;
          }
          if (addNewUser){
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

}

  public List<User> getListUsers() {
    return users;
  }

  public List<Conversation> getListConversations() {
    return conversations;
  }

  public List<Message> getListMessages() {
    return messages;
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
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


  private void readFile1(String filename) throws Exception
  {
      String line = null;
      // wrap a BufferedReader around FileReader
      BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
      boolean addToLine = false;
      boolean alreadySawSpecialChar = false;
      // use the readLine method of the BufferedReader to read one line at a time.
      // the readLine method returns null when there is nothing else to read.
      while ((line = bufferedReader.readLine()) != null)
      {
        boolean addNewUser = false;
        //TODO: conversation @ new SCENE

        String[] currLine = line.split("\\s");
        String firstWord = currLine[0];
        System.out.println("this is first word: " );
        System.out.print(firstWord);
      //  System.out.println(firstWord);
        if (checkIfAllCapital(firstWord)){
          //TODO: check what kinda capital word it is
            addToLine = true;
            String userName = line;
            switch(firstWord){

              case "ACT":
              System.out.println("gound ACT correctly");
              //new CONVERSATION
              if (userStore.getUser("NARRATOR")==null){
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
              conversations.add(conversation);
              break;
              case "SCENE":
              userName = "NARRATOR";
              addNewUser = true;
              break;
              //USER = narrator
              default://is new USER  or old????
              addNewUser = true;
            }
            if (addNewUser){
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

      // close the BufferedReader when we're done
      bufferedReader.close();
  }

  /*Takes out capital words letters & sees if they match with "Act", "Scene" or character name*/
  boolean checkIfAllCapital(String firstWord){

    for(int i = 0; i < firstWord.length(); i++){
       if(Character.isLetter(firstWord.charAt(i)) && Character.isUpperCase(firstWord.charAt(i))){
          //Then is a title/scene/character/
          return true;
      }
    }
    return false;
  }

  boolean checkSpecialCharacter(String word){
    //   switch(word){
    //     case "ACT":break; //Start New Conversation
    //     case "SCENE":break;//Start
    //     default: "USER"
    //     //else character name ex: FRIAR LAURENCE add user
    //
    // }
    return false;
  }

  // boolean checkSpecialCharacter(String word){
  //   //   switch(word.toLowerCase()){
  //   //     case "enter":break; //Start New Conversation
  //   //     case "exeunt":break;//Start
  //   //     case "exit":break;
  //   //     case "re-enter":break;
  //   //     case "**......":break;
  //   //
  //   // }
  //   return false;
  // }
  //he goeth downright
  //layin down her dagger


}
