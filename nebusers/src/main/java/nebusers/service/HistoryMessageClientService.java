package nebusers.service;

import java.util.List;

import nebusers.dto.HistoryMessageDTO;


public interface HistoryMessageClientService {

	void postHistoryMessage(Long userId, String message) throws Exception;
	
	List<HistoryMessageDTO> getHistoryMessagesById(Long userId) throws Exception;

}
