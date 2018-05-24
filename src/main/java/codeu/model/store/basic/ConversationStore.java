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

package codeu.model.store.basic;

import codeu.model.data.Conversation;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.util.UUID;
/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class ConversationStore {

  /** Singleton instance of ConversationStore. */
  private static ConversationStore instance;

  /**
   * Returns the singleton instance of ConversationStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static ConversationStore getInstance() {
    if (instance == null) {
      instance = new ConversationStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static ConversationStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new ConversationStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Conversations from and saving Conversations
   * to Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Conversations. */
  private List<Conversation> conversations;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private ConversationStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    conversations = new ArrayList<>();
  }

  /**
   * Load a set of randomly-generated Conversation objects.
   *
   * @return false if a error occurs.
   */
  public boolean loadTestData() {
    boolean loaded = false;
    try {
      conversations.addAll(DefaultDataStore.getInstance().getAllConversations());
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.err.println("ERROR: Unable to establish initial store (conversations).");
    }
    return loaded;
  }

  /** Access the current set of conversations known to the application. */
  public List<Conversation> getAllConversations() {
    return conversations;
  }

  /** Add a new conversation to the current set of conversations known to the application. */
  public void addConversation(Conversation conversation) {
    conversations.add(conversation);
    persistentStorageAgent.writeThrough(conversation);
  }

  /*Removes from in-store conversation list to update*/
  public void removeConversationFromInStoreList(Conversation conversation){
    conversations.remove(conversation);
  }

  /** Check whether a Conversation title is already known to the application. */
  public boolean isTitleTaken(String title) {
    // This approach will be pretty slow if we have many Conversations.
    for (Conversation conversation : conversations) {
      if (conversation.getTitle().equals(title)) {
        return true;
      }
    }
    return false;
  }

  /** Find and return the Conversation with the given title. */
  public Conversation getConversationWithTitle(String title) {
    for (Conversation conversation : conversations) {
      if (conversation.getTitle().equals(title)) {
        return conversation;
      }
    }
    return null;
  }

  /** Returns Conversation retrieved from Persistent Database*/
  public Conversation getConversationFromPD(UUID convoUUID) throws EntityNotFoundException{
    String convoUUIDString = convoUUID.toString();
    return persistentStorageAgent.getConversationFromPD(convoUUIDString);
  }

  /** Sets the List of Conversations stored by this ConversationStore. */
  public void setConversations(List<Conversation> conversations) {
    this.conversations = conversations;
  }

  /**Returns the number of conversations total*/
  public int getNumConversations(){
    if (conversations!=null){
      return conversations.size();
    }
    return 0;
  }

  /** Adds member to this Conversation*/
  public void addMemberinPD(UUID convoUUID, UUID newUser) throws EntityNotFoundException{
    System.out.println("in add mbr pd");
    String convoUUIDString = convoUUID.toString();
    System.out.println("this is convouui.tostring " + convoUUIDString);
    Conversation convo =  persistentStorageAgent.getConversationFromPD(convoUUIDString);
    System.out.println("getting past gettingconvofrompd");
    System.out.println(convo.getMembers().size());
    convo.addMember(newUser);
    System.out.println(convo.getMembers().size());
    //Conversation newConvo = new Conversation (convoUUID, convo.getOwnerId(), convo.getTitle(), convo.getCreationTime(), convo.getMembers());
    //UUID id, UUID owner, String title, Instant creation, Set<UUID> member
    persistentStorageAgent.updateConversationMembers(convo);
    //updatePersistenDatabase
  }

  /** Removes a member from the members list*/
  public void removeMemberinPD(UUID removeUser) throws EntityNotFoundException{
    // members.remove(removeUser);
    // updateConversationMembers(this);
    //updatePersistentDatabase
  }
}
