package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

public class LogOutServletTest {

  private LogOutServlet logOutServlet;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private HttpSession mockSession;

  @Before
  public void setup() {
    logOutServlet = new LogOutServlet();
    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
  }



  @Test
  public void testDoGet_NonExistingUser() throws IOException, ServletException {
    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("test username")).thenReturn(false);
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test username");
    logOutServlet.setUserStore(mockUserStore);

    logOutServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockResponse).sendRedirect("/");
  }

  @Test
  public void testDoPost_ExistingUser() throws IOException, ServletException {
    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("test username")).thenReturn(true);
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test username");
    logOutServlet.setUserStore(mockUserStore);

    logOutServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockSession).removeAttribute("user");
    Mockito.verify(mockSession).removeAttribute("is_admin");
    Mockito.verify(mockResponse).sendRedirect("/");
  }
}
