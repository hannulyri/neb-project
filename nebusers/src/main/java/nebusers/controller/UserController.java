package nebusers.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nebusers.dto.HistoryMessageDTO;
import nebusers.dto.UserDTO;
import nebusers.dto.UserInformationDTO;
import nebusers.model.User;
import nebusers.model.ValidationError;
import nebusers.repository.UserRepository;
import nebusers.resource.HistoryMessageResource;
import nebusers.resource.HistoryMessageResourceAssembler;
import nebusers.resource.UserResource;
import nebusers.resource.UserResourceAssembler;
import nebusers.service.HistoryMessageClientService;
import nebusers.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping("/api")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
	private final UserRepository userRepository;
	private final UserService userService;
    private final UserResourceAssembler userResourceAssembler;
    private final HistoryMessageClientService historyMessageClientService;
    private final HistoryMessageResourceAssembler historyMessageResourceAssembler;
    
    @Inject
    public UserController(
    		UserRepository userRepository,
    		UserService userService,
    		UserResourceAssembler userResourceAssembler,
    		HistoryMessageClientService historyMessageClientService,
    		HistoryMessageResourceAssembler historyMessageResourceAssembler
    		) {
    	this.userRepository = userRepository;
    	this.userService = userService;
    	this.userResourceAssembler = userResourceAssembler;
    	this.historyMessageClientService = historyMessageClientService;
    	this.historyMessageResourceAssembler = historyMessageResourceAssembler;
    }

    @RequestMapping(value = "/users",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> registerAccount(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
        	LOGGER.debug("Registration validation fail: {}", new Gson().toJson(ValidationError.of(bindingResult)));
            return new ResponseEntity<Object>(new Gson().toJson(ValidationError.of(bindingResult)), HttpStatus.BAD_REQUEST);
        }

        return userRepository.findOneByLogin(userDTO.getLogin())
            .map(user -> new ResponseEntity<Object>("login already in use", HttpStatus.BAD_REQUEST))
            .orElseGet(() -> userRepository.findOneByEmail(userDTO.getEmail())
                .map(user -> new ResponseEntity<Object>("e-mail address already in use", HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    User user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                    userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase());
                	LOGGER.debug("Adding new user: {} {}", user.getLogin(), user.getEmail());
                	return new ResponseEntity<>(userResourceAssembler.toResource(user), HttpStatus.CREATED);
                })
        );
    }
    
    @RequestMapping(
    		method = RequestMethod.GET, 
    		value = "/users", 
    		produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity<PagedResources<UserResource>> getUsers(
            @PageableDefault(size = 5, page = 0) Pageable pageable,
            PagedResourcesAssembler<User> assembler) {
    	return new ResponseEntity<>(assembler.toResource(userRepository.findAll(pageable), userResourceAssembler), HttpStatus.OK);
    } 
    
    @RequestMapping(
    		method = RequestMethod.GET, 
    		value = "/users/{userId}/history", 
    		produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity<List<HistoryMessageResource>> getHistoryMessagesByUserId(
    		@PathVariable("userId") Long userId) throws Exception {
    	List<HistoryMessageResource> resources =  new ArrayList<HistoryMessageResource>();
    	for (HistoryMessageDTO message : historyMessageClientService.getHistoryMessagesById(userId)) {
    		resources.add(historyMessageResourceAssembler.toResource(message));
    	}  			
    	return new ResponseEntity<List<HistoryMessageResource>>(resources, HttpStatus.OK);
    }      
    
    @RequestMapping(
    		method = RequestMethod.GET, 
    		value = "/users/{userId}",
    		produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity<UserResource> getUserById(
    		@PathVariable("userId") Long userId) throws ResourceNotFoundException {
    	return userRepository.findOneById(userId)
                .map(user -> new ResponseEntity<>(userResourceAssembler.toResource(user), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));    	
    }
    
    @RequestMapping(
    		method = RequestMethod.PUT,
    		value = "/users/{userId}",            
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveAccount(@Valid @RequestBody UserInformationDTO userInformationDTO, @PathVariable("userId") Long userId) {
        return userRepository
            .findOneById(userId)
            .map(u -> {
                userService.updateUserInformation(userId, userInformationDTO.getFirstName(), userInformationDTO.getLastName(), userInformationDTO.getEmail());
                return new ResponseEntity<String>(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }    
        
    @RequestMapping(
    		method = RequestMethod.DELETE,
    		value = "/users/{userId}",
    		produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> deleteUser(
    		@PathVariable("userId") Long userId) throws ResourceNotFoundException {
    	
    	return userRepository.findOneById(userId)
                .map(user -> {
                	LOGGER.debug("User deleted: {}", userId);
                	userRepository.delete(userId);
                	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));        	
    }    

}
