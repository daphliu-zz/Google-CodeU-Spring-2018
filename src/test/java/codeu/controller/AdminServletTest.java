
package codeu.controller;

import static org.mockito.Mockito.never;

import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentDataStoreException;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AdminServletTest {

  private AdminServlet adminStats;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ConversationStore mockConversationStore;
  private MessageStore mockMessageStore;
  private UserStore mockUserStore;

  @Before
  public void setup() {
    adminStats = new AdminServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/admin.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    adminStats.setConversationStore(mockConversationStore);

    mockMessageStore = Mockito.mock(MessageStore.class);
    adminStats.setMessageStore(mockMessageStore);

    mockUserStore = Mockito.mock(UserStore.class);
    adminStats.setUserStore(mockUserStore);
  }

  private void adminIsLoggedIn() {
    Mockito.when(mockSession.getAttribute("user")).thenReturn("admin");
    Mockito.when(mockUserStore.getUser("admin"))
        .thenReturn(new User(UUID.randomUUID(), "admin", "password", Instant.now(), true));
  }

  @Test
  public void testDoPost_notLoggedIn() throws IOException, ServletException {
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockResponse).sendRedirect("/");
  }

  @Test
  public void testDoPost_userIsLoggedIn() throws IOException, ServletException {
    Mockito.when(mockSession.getAttribute("user")).thenReturn("not_admin");
    User fakeUser = new User(UUID.randomUUID(), "not_admin", "password", Instant.now(), false);
    Mockito.when(mockUserStore.getUser("not_admin")).thenReturn(fakeUser);

    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockResponse).sendRedirect("/");
  }

  @Test
  public void testDoPost_InvalidUserName() throws IOException, ServletException {
    adminIsLoggedIn();
    Mockito.when(mockRequest.getParameter("change_admin_status")).thenReturn("promote");
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("error", "Not a user.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_nullButtons() throws IOException, ServletException {
    adminIsLoggedIn();
    Mockito.when(mockRequest.getParameter("change_admin_status")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("confirm")).thenReturn(null);
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest, never()).setAttribute("error", "Not a user.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_Promote()
      throws IOException, ServletException, PersistentDataStoreException {
    adminIsLoggedIn();
    Mockito.when(mockRequest.getParameter("change_admin_status")).thenReturn("promote");
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    User fakeUser = new User(UUID.randomUUID(), "test_username", "password", Instant.now(), false);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockUserStore).setIsAdmin(fakeUser, true);
    Mockito.verify(mockRequest).setAttribute("success", "Promoted the user to admin!");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_PromoteAdmin()
      throws IOException, ServletException, PersistentDataStoreException {
    adminIsLoggedIn();
    Mockito.when(mockRequest.getParameter("change_admin_status")).thenReturn("promote");
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    User fakeUser = new User(UUID.randomUUID(), "test_username", "password", Instant.now(), true);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("error", "User is already an admin.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_Demote()
      throws IOException, ServletException, PersistentDataStoreException {
    adminIsLoggedIn();
    Mockito.when(mockRequest.getParameter("change_admin_status")).thenReturn("demote");
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    User fakeUser = new User(UUID.randomUUID(), "test_username", "password", Instant.now(), true);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockUserStore).setIsAdmin(fakeUser, false);
    Mockito.verify(mockRequest).setAttribute("success", "Demoted the admin to user :(");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_DemoteAdmin()
      throws IOException, ServletException, PersistentDataStoreException {
    adminIsLoggedIn();
    Mockito.when(mockRequest.getParameter("change_admin_status")).thenReturn("demote");
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    User fakeUser = new User(UUID.randomUUID(), "test_username", "password", Instant.now(), false);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    adminStats.doPost(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("error", "User is already not an admin.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
}
