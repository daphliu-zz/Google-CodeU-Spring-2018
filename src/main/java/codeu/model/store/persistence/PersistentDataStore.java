// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.model.store.persistence;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class handles all interactions with Google App Engine's Datastore service. On startup it
 * sets the state of the applications's data objects from the current contents of its Datastore. It
 * also performs writes of new of modified objects back to the Datastore.
 */
public class PersistentDataStore {

  // Handle to Google AppEngine's Datastore service.
  private DatastoreService datastore;

  /**
   * Constructs a new PersistentDataStore and sets up its state to begin loading objects from the
   * Datastore service.
   */
  public PersistentDataStore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /* Retrieves a User Object given User UUID as string */
  public User getUserFromPDatabase(String userN) throws EntityNotFoundException {
    Key key = KeyFactory.createKey("chat-users", userN);
    Entity entity = datastore.get(key);
    UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
    String userName = (String) entity.getProperty("username");
    boolean is_admin = (boolean) entity.getProperty("is_admin");
    String password = (String) entity.getProperty("password");
    Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
    User user = new User(uuid, userName, password, creationTime, is_admin);
    return user;
  }

  /**
   * Loads all User objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<User> loadUsers() throws PersistentDataStoreException {

    List<User> users = new ArrayList<>();

    // Retrieve all users from the datastore.
    // & sorts users in retrieval of database by creation time to make getting
    // latest user added retrieval simpler
    Query query = new Query("chat-users").addSort("creation_time");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        String userName = (String) entity.getProperty("username");
        String password = (String) entity.getProperty("password");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        // is_admin may be null for the users already exist in datastore
        // is_admin is casted as an Object to do null check
        Boolean is_admin = (Boolean) entity.getProperty("is_admin");
        if (is_admin == null) is_admin = false;
        // User constructor takes in is_admin as primitive so would be converted
        // utomatically
        User user = new User(uuid, userName, password, creationTime, is_admin);
        users.add(user);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return users;
  }

  /*Retrieves a Conversation Object given Convo UUID as string*/
  public Conversation getConversationFromPD(String convoUUID) throws EntityNotFoundException {
    Key key = KeyFactory.createKey("chat-conversations", convoUUID);
    Entity entity = datastore.get(key);
    UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
    UUID ownerUuid = UUID.fromString((String) entity.getProperty("owner_uuid"));
    String title = (String) entity.getProperty("title");
    Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
    String[] membersAsStrings = ((String) entity.getProperty("members")).split(",", 0);
    Set<UUID> members = new HashSet<UUID>();
    // gets membersAsStrings and changes to UUIDs and puts into set
    for (int i = 0; i < membersAsStrings.length; i++) {
      if (i == 0) {
        membersAsStrings[i] = membersAsStrings[i].substring(1);
      }
      if (i == membersAsStrings.length - 1) {
        membersAsStrings[i] = membersAsStrings[i].substring(0, membersAsStrings[i].length() - 1);
      }
      // gets rid of whitespace from string array in order to cast as UUID
      String stringVal = membersAsStrings[i].replaceAll("\\s+", "");
      UUID val = UUID.fromString(stringVal);
      members.add(val);
    }

    Conversation conversation = new Conversation(uuid, ownerUuid, title, creationTime, members);
    return conversation;
  }

  /**
   * Loads all Conversation objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<Conversation> loadConversations() throws PersistentDataStoreException {

    List<Conversation> conversations = new ArrayList<>();

    // Retrieve all conversations from the datastore.
    // & sorts conversations in database by creation time to have conversations
    // display in order created in order to make sense to the user
    Query query = new Query("chat-conversations").addSort("creation_time");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID ownerUuid = UUID.fromString((String) entity.getProperty("owner_uuid"));
        String title = (String) entity.getProperty("title");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String[] membersAsStrings = ((String) entity.getProperty("members")).split(",", 0);
        Set<UUID> members = new HashSet<UUID>();
        // gets membersAsStrings and changes to UUIDs and puts into set
        for (int i = 0; i < membersAsStrings.length; i++) {
          if (i == 0) {
            membersAsStrings[i] = membersAsStrings[i].substring(1);
          }
          if (i == membersAsStrings.length - 1) {
            membersAsStrings[i] =
                membersAsStrings[i].substring(0, membersAsStrings[i].length() - 1);
          }
          // gets rid of whitespace from string array in order to cast as UUID
          String stringVal = membersAsStrings[i].replaceAll("\\s+", "");
          UUID val = UUID.fromString(stringVal);
          members.add(val);
        }
        Conversation conversation = new Conversation(uuid, ownerUuid, title, creationTime, members);
        conversations.add(conversation);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return conversations;
  }

  /*gets a given conversation object and updates it with new info mostly for members update*/
  public void updateConversationMembers(Conversation conversation) throws EntityNotFoundException {
    String uuid = conversation.getId().toString();
    Key key = KeyFactory.createKey("chat-conversations", uuid);
    Entity conversationEntity = datastore.get(key);
    conversationEntity.setProperty("uuid", conversation.getId().toString());
    conversationEntity.setProperty("owner_uuid", conversation.getOwnerId().toString());
    conversationEntity.setProperty("title", conversation.getTitle());
    conversationEntity.setProperty("creation_time", conversation.getCreationTime().toString());
    conversationEntity.setProperty("members", conversation.getMembers().toString());
    datastore.put(conversationEntity);
  }

  /**
   * Loads all Message objects from the Datastore service and returns them in a List.
   *
   * @throws codeu.model.store.persistence.PersistentDataStoreException if an error was detected
   *     during the load from the Datastore service
   */
  public List<Message> loadMessages() throws PersistentDataStoreException {

    List<Message> messages = new ArrayList<>();

    // Retrieve all messages from the datastore.
    // & sorts messages in database by creation time to display the flow of the
    // messages in the order that makes sense to the user
    Query query = new Query("chat-messages").addSort("creation_time");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID conversationUuid = UUID.fromString((String) entity.getProperty("conv_uuid"));
        UUID authorUuid = UUID.fromString((String) entity.getProperty("author_uuid"));
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String content = (String) entity.getProperty("content");
        Message message = new Message(uuid, conversationUuid, authorUuid, content, creationTime);
        messages.add(message);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }
    return messages;
  }

  /** Write a new User object to the Datastore service. */
  public void createUser(User user) {
    String uuid = user.getId().toString();
    Entity userEntity = new Entity("chat-users", uuid);
    userEntity.setProperty("uuid", uuid);
    userEntity.setProperty("username", user.getName());
    userEntity.setProperty("password", user.getHashedPassword());
    userEntity.setProperty("creation_time", user.getCreationTime().toString());
    userEntity.setProperty("is_admin", user.getAdminStatus());
    datastore.put(userEntity);
  }

  /** Update a User object to the Datastore service. */
  public void updateUserAdminStatus(User user, boolean is_admin) throws EntityNotFoundException {
    String uuid = user.getId().toString();
    Key key = KeyFactory.createKey("chat-users", uuid);
    Entity userEntity = datastore.get(key);
    userEntity.setProperty("uuid", uuid);
    userEntity.setProperty("username", user.getName());
    userEntity.setProperty("password", user.getHashedPassword());
    userEntity.setProperty("creation_time", user.getCreationTime().toString());
    userEntity.setProperty("is_admin", is_admin);
    datastore.put(userEntity);
  }

  /** Write a Message object to the Datastore service. */
  public void writeThrough(Message message) {
    Entity messageEntity = new Entity("chat-messages");
    messageEntity.setProperty("uuid", message.getId().toString());
    messageEntity.setProperty("conv_uuid", message.getConversationId().toString());
    messageEntity.setProperty("author_uuid", message.getAuthorId().toString());
    messageEntity.setProperty("content", message.getContent());
    messageEntity.setProperty("creation_time", message.getCreationTime().toString());
    datastore.put(messageEntity);
  }

  /** Write a Conversation object to the Datastore service. */
  public void writeThrough(Conversation conversation) {
    Entity conversationEntity =
        new Entity("chat-conversations", conversation.getId().toString()); // query based on UUID
    conversationEntity.setProperty("uuid", conversation.getId().toString());
    conversationEntity.setProperty("owner_uuid", conversation.getOwnerId().toString());
    conversationEntity.setProperty("title", conversation.getTitle());
    conversationEntity.setProperty("creation_time", conversation.getCreationTime().toString());
    conversationEntity.setProperty("members", conversation.getMembers().toString());
    datastore.put(conversationEntity);
  }
}
