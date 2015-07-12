package nebusers.service;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import nebusers.controller.UserController;
import nebusers.model.Authority;
import nebusers.model.User;
import nebusers.repository.AuthorityRepository;
import nebusers.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private AuthorityRepository authorityRepository;
    
	@Inject
	private HistoryMessageClientService historyMessageClientService;    

    public User createUserInformation(String login, String password, String firstName, String lastName, String email) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        authorities.add(authority);
        newUser.setAuthorities(authorities);       
        userRepository.save(newUser);
        LOGGER.debug("Created Information for User: {}", newUser);
        
        try {        	
        	historyMessageClientService.postHistoryMessage(newUser.getId(), "User registrated");
        } catch (Exception e) {
        }
        
        
        return newUser;
    }

    public void updateUserInformation(Long id, String firstName, String lastName, String email) {
        userRepository.findOneById(id).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            LOGGER.debug("Changed Information for User: {}", u);
        });
    }
}
