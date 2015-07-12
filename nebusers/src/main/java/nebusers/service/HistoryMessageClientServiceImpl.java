package nebusers.service;

import java.util.List;

import javax.inject.Inject;

import nebusers.dto.HistoryMessageDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HistoryMessageClientServiceImpl implements HistoryMessageClientService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryMessageClientServiceImpl.class);
	
	//@Inject
	//private final RestTemplate restTemplate;
	
    @Inject
    private Environment env;	
    /*
    @Inject
    public HistoryMessageClientServiceImpl() {
    	this.restTemplate = new RestTemplate();    
    }  
    */

    public void postHistoryMessage(Long userId, String message) throws Exception{
    	HistoryMessageDTO dto = new HistoryMessageDTO(userId, message);
    	
    	RestTemplate restTemplate = new RestTemplate();

    	restTemplate.postForObject(env.getRequiredProperty("nebistory.history.url"), dto, HistoryMessageDTO.class);		
    }

	@Override
	public List<HistoryMessageDTO> getHistoryMessagesById(Long userId) throws Exception {
    	RestTemplate restTemplate = new RestTemplate();
		ParameterizedTypeReference<List<HistoryMessageDTO>> responseType = new ParameterizedTypeReference<List<HistoryMessageDTO>>() {};
		ResponseEntity<List<HistoryMessageDTO>> result = restTemplate.exchange(env.getRequiredProperty("nebistory.history.url") + "/search/?userId=" + userId, HttpMethod.GET, null, responseType);
		List<HistoryMessageDTO> list = result.getBody();
		return list;
	}
}
