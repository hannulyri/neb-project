package nebhistory.service;

import java.util.Optional;

import javax.inject.Inject;

import nebhistory.controller.HistoryMessageController;
import nebhistory.model.HistoryMessage;
import nebhistory.repository.HistoryMessageRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HistoryMessageServiceImpl implements HistoryMessageService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryMessageServiceImpl.class);
	
	private final HistoryMessageRepository historyMessageRepository;
    
    @Inject
    public HistoryMessageServiceImpl(
    		HistoryMessageRepository historyMessageRepository
    		) {
    	this.historyMessageRepository = historyMessageRepository;
    } 

	@Override
	public HistoryMessage createHistoryMessage(Long userId, String message) {
		LOGGER.debug("HistoryMessage added: {}, {}", userId, message);
		return historyMessageRepository.save(new HistoryMessage(userId, message));
	}
}
