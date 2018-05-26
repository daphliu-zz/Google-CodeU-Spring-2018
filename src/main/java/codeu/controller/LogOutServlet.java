
package codeu.controller;

import codeu.model.store.basic.UserStore;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for logout. */
public class LogOutServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /**
   * Set up state for handling LOGOUT-related requests. This method is only called
   * when running in a server, not when running in a test.
   */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common
   * setup method for use by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * This function fires when a user requests the /logout URL.
   * Logs the user out then redirects to homepage.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // log user out
    String username = (String) request.getSession().getAttribute("user");

    if (userStore.isUserRegistered(username)) {
      request.getSession().removeAttribute("user");
      request.getSession().removeAttribute("is_admin");
    }

    // redirect to homepage
    response.sendRedirect("/");
  }
}
