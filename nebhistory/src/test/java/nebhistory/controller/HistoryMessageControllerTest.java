package nebhistory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import nebhistory.NebhistoryApplication;
import nebhistory.dto.HistoryMessageDTO;
import nebhistory.model.HistoryMessage;
import nebhistory.repository.HistoryMessageRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NebhistoryApplication.class)
@WebAppConfiguration
@IntegrationTest
public class HistoryMessageControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private MockMvc mockMvc;
    

	@Inject
	private HistoryMessageRepository historyMessageRepository;

    @Before
    public void setup() {
    	
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(converters);

        this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();    	
    }
    
    @Test
    @Transactional
    public void createValidHistoryMessage() throws Exception {
    	Long userId = 1000L;
    	String message = "Hello World!";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(Integer.valueOf(userId.intValue())))
                .andExpect(jsonPath("$.message").value(message))
                ;

        Optional<HistoryMessage> hh = historyMessageRepository.findOneByUserId(userId);
        assertThat(hh.isPresent()).isTrue();    
        
        this.mockMvc.perform
	        (get("/api/history/" + hh.get().getId())
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.userId").value(Integer.valueOf(userId.intValue())))
	        .andExpect(jsonPath("$.message").value(message))
	        ;
    } 
    
    @Test
    @Transactional
    public void createValidHistoryMessageAndDeleteIt() throws Exception {
    	Long userId = 1000L;
    	String message = "Hello World!";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(Integer.valueOf(userId.intValue())))
                .andExpect(jsonPath("$.message").value(message))
                ;

        Optional<HistoryMessage> hh = historyMessageRepository.findOneByUserId(userId);
        assertThat(hh.isPresent()).isTrue();    
        
        this.mockMvc.perform
	        (delete("/api/history/" + hh.get().getId())
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isNoContent())
	        ;
        
        this.mockMvc.perform
	        (delete("/api/history/" + hh.get().getId())
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isNotFound())
	        ;        
    }     
    
    @Test
    @Transactional
    public void InvalidHistoryMessageTooLowUserId() throws Exception {
    	Long userId = 0L;
    	String message = "Hello World!";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isBadRequest());
        
        Optional<HistoryMessage> hh = historyMessageRepository.findOneByUserId(userId);
        assertThat(hh).isEmpty();     
    }   
    
    @Test
    @Transactional
    public void InvalidHistoryMessageTooShortMessage() throws Exception {
    	Long userId = 1000L;
    	String message = "";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isBadRequest());
        
        Optional<HistoryMessage> hh = historyMessageRepository.findOneByUserId(userId);
        assertThat(hh).isEmpty();     
    }   
    
    @Test
    @Transactional
    public void InvalidHistoryMessageTooLongMessage() throws Exception {
    	Long userId = 1000L;
    	String message = "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isBadRequest());
        
        Optional<HistoryMessage> hh = historyMessageRepository.findOneByUserId(userId);
        assertThat(hh).isEmpty();     
    }
    
    @Test
    @Transactional
    public void InvalidHistoryMessageEmpty() throws Exception {
        HistoryMessageDTO dto = new HistoryMessageDTO();
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isBadRequest());    
    }   
    
    @Test
    @Transactional
    public void checkHistory() throws Exception {
    	Long userId = 1000L;
    	String message = "Hello World!";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());
        
        this.mockMvc.perform
	        (post("/api/history")
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(dto))
	        )
	        .andExpect(status().isCreated());   
        
        this.mockMvc.perform
	        (get("/api/history")
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$._embedded.historyMessages[0].userId").exists())
	        .andExpect(jsonPath("$._embedded.historyMessages[0].message").exists())
	        .andExpect(jsonPath("$._embedded.historyMessages[1].userId").exists())
	        .andExpect(jsonPath("$._embedded.historyMessages[1].message").exists())	
	        ;
    } 

    @Test
    @Transactional
    public void checkHistoryUserIdSearchWorks() throws Exception {
    	Long userId = 1000L;
    	String message = "Hello World!";
    	
        HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
               
        this.mockMvc.perform
                (post("/api/history")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(dto))
                )
                .andExpect(status().isCreated());
        
        this.mockMvc.perform
	        (get("/api/history/search/?userId=" + userId)
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$..userId").value(Integer.valueOf(userId.intValue())))
	        .andExpect(jsonPath("$..message").value(message))
	        ;
    }     
    
}
