package nebusers.security;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nebusers.controller.UserController;
import nebusers.model.User;
import nebusers.repository.UserRepository;
import nebusers.service.HistoryMessageClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private HistoryMessageClientService historyMessageClientService;	

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {
    	
    	Optional<User> u = userRepository.findOneByLogin(authentication.getName());

        try {        	
        	LOGGER.debug("User logout: {}", u.get().getId());
        	historyMessageClientService.postHistoryMessage(u.get().getId(), "User logout");
        } catch (Exception e) {
        }
    	
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
