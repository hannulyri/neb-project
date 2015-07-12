package nebusers.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.transaction.Transactional;

import nebusers.NebusersApplication;
import nebusers.dto.UserDTO;
import nebusers.dto.UserInformationDTO;
import nebusers.model.User;
import nebusers.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NebusersApplication.class)
@WebAppConfiguration
@IntegrationTest
public class UserControllerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

	@Inject
    private WebApplicationContext wac;
    
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private MockMvc mockMvc;
    
    @Inject
    private Filter springSecurityFilterChain;    
    
	@Inject
	private UserRepository userRepository;

    @Before
    public void setup() {
    	
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(converters);

        this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
        this.mockMvc = 
        		MockMvcBuilders.webAppContextSetup(this.wac)
        		.addFilter(springSecurityFilterChain)
        		.build();
    }
    
    @Test
    @Transactional
    public void createValidUser() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.email").value(email))                
                ;

        Optional<User> hh = userRepository.findOneByLogin(login);
        assertThat(hh.isPresent()).isTrue();    

        this.mockMvc.perform
	        (get("/api/users/" + hh.get().getId())
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value(login))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.firstName").value(firstName))
            .andExpect(jsonPath("$.lastName").value(lastName))
            .andExpect(jsonPath("$.email").value(email))      
	        ;
    } 
    
    @Test
    @Transactional
    public void verifyNoDuplicateLogin() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());
        
        this.mockMvc.perform
	        (post("/api/users")
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(dto))
	        )
	        .andExpect(status().isBadRequest())
	        .andExpect(content().string("login already in use") )
	        ;        
    } 
    
    @Test
    @Transactional
    public void verifyNoDuplicateEmail() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());
        
        UserDTO dto2 = new UserDTO("barlogin2", password, firstName, lastName, email);
        
        this.mockMvc.perform
	        (post("/api/users")
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(dto2))
	        )
	        .andExpect(status().isBadRequest())
	        .andExpect(content().string("e-mail address already in use") )
	        ;        
    }     
    
    @Test
    @Transactional
    public void missingData() throws Exception {
    	String login = "";
    	String password = "";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isBadRequest());      
    } 
    
    @Test
    @Transactional
    public void checkUsers() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());

        UserDTO dto2 = new UserDTO("barlogin2", password, firstName, lastName, "barmail2@mail.com");

        this.mockMvc.perform
	        (post("/api/users")
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(dto2))
	        )
	        .andExpect(status().isCreated());        
        
        this.mockMvc.perform
	        (get("/api/users")
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$._embedded.users[0].login").exists())
	        .andExpect(jsonPath("$._embedded.users[0].password").doesNotExist())
	        .andExpect(jsonPath("$._embedded.users[0].firstName").exists())
	        .andExpect(jsonPath("$._embedded.users[0].lastName").exists())
	        .andExpect(jsonPath("$._embedded.users[0].email").exists())
	        .andExpect(jsonPath("$._embedded.users[1].login").exists())
	        .andExpect(jsonPath("$._embedded.users[1].password").doesNotExist())
	        .andExpect(jsonPath("$._embedded.users[1].firstName").exists())
	        .andExpect(jsonPath("$._embedded.users[1].lastName").exists())
	        .andExpect(jsonPath("$._embedded.users[1].email").exists())	
	        ;
    }  
    
    @Test
    @Transactional
    public void createUserAndDeleteIt() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());

        Optional<User> hh = userRepository.findOneByLogin(login);
        assertThat(hh.isPresent()).isTrue();  
        
        this.mockMvc.perform
        (get("/api/users/" + hh.get().getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)           
        )
        .andExpect(status().isOk());        

        this.mockMvc.perform
        (delete("/api/users/" + hh.get().getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)           
        )
        .andExpect(status().isNoContent());
        
        this.mockMvc.perform
        (delete("/api/users/" + hh.get().getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)           
        )
        .andExpect(status().isNotFound());      
        
        Optional<User> hh2 = userRepository.findOneByLogin(login);
        assertThat(hh2.isPresent()).isFalse();          
        
    }   
    
    @Test
    @Transactional
    public void createValidUserAndUpdateIt() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.email").value(email))                
                ;

        Optional<User> hh = userRepository.findOneByLogin(login);
        assertThat(hh.isPresent()).isTrue();   

    	String firstNameU = "ubarfirstname";
    	String lastNameU = "ubarlastname";
    	String emailU = "ubaremail@mail.com";       
        
        UserInformationDTO dto2 = new UserInformationDTO(firstNameU, lastNameU, emailU);

        this.mockMvc.perform
	        (put("/api/users/" + hh.get().getId())
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(dto2))
	        )
	        .andExpect(status().isOk());
        
        this.mockMvc.perform
	        (get("/api/users/" + hh.get().getId())
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(dto2))
	        )
	        .andExpect(status().isOk())        
            .andExpect(jsonPath("$.login").value(login))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.firstName").value(firstNameU))
            .andExpect(jsonPath("$.lastName").value(lastNameU))
            .andExpect(jsonPath("$.email").value(emailU))      
	        ;
    }    
    

    @Test
    @Transactional
    public void createValidUserAndLogin() throws Exception {
    	String login = "barlogin";
    	String password = "barpassword";
    	String firstName = "barfirstname";
    	String lastName = "barlastname";
    	String email = "baremail@mail.com";
    	
        UserDTO dto = new UserDTO(login, password, firstName, lastName, email);
               
        this.mockMvc.perform
                (post("/api/users")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());
               
        this.mockMvc.perform
	        (post("/api/authentication") 
	        		//.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        		.param("login", login)
	        		.param("password", password)
	        )
	        .andDo(print())
	        .andExpect(status().isOk())
	        .andReturn()
	        ;
     }        
    
}
