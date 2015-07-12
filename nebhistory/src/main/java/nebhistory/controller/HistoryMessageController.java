package nebhistory.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import nebhistory.dto.HistoryMessageDTO;
import nebhistory.model.HistoryMessage;
import nebhistory.model.ValidationError;
import nebhistory.repository.HistoryMessageRepository;
import nebhistory.resource.HistoryMessageResource;
import nebhistory.resource.HistoryMessageResourceAssembler;
import nebhistory.service.HistoryMessageService;

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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

@Controller
public class HistoryMessageController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryMessageController.class);
    
	private final HistoryMessageRepository historyMessageRepository;
	private final HistoryMessageService historyMessageService;
    private final HistoryMessageResourceAssembler historyMessageResourceAssembler;
    private final PagedResourcesAssembler<HistoryMessage> assembler;
    
    @Inject
    public HistoryMessageController(
    		HistoryMessageRepository historyMessageRepository,
    		HistoryMessageService historyMessageService,
    		HistoryMessageResourceAssembler historyMessageResourceAssembler,
    		PagedResourcesAssembler<HistoryMessage> assembler
    		) {
    	this.historyMessageRepository = historyMessageRepository;
    	this.historyMessageService = historyMessageService;
    	this.historyMessageResourceAssembler = historyMessageResourceAssembler;
    	this.assembler = assembler;
    }    
    
    @RequestMapping(
    		method = RequestMethod.POST,
    		value = "/api/history",
    		produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> createHistoryMessage(
    		@Valid @RequestBody HistoryMessageDTO historyMessageDTO, BindingResult bindingResult) throws ResourceNotFoundException {
	        if (bindingResult.hasErrors()) {
	        	LOGGER.debug("Validation fail: {}", new Gson().toJson(ValidationError.of(bindingResult)));
	            return new ResponseEntity<Object>(new Gson().toJson(ValidationError.of(bindingResult)), HttpStatus.BAD_REQUEST);
	        }    	
	        return Optional.of(historyMessageService.createHistoryMessage(historyMessageDTO.getUserId(), historyMessageDTO.getMessage()))
	                .map(historyMessage -> new ResponseEntity<Object>(historyMessageResourceAssembler.toResource(historyMessage), HttpStatus.CREATED))
	                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));	        
	        
    }    

    @RequestMapping(
    		method = RequestMethod.GET, 
    		value = "/api/history", 
    		produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity<PagedResources<HistoryMessageResource>> getHistoryMessages(
            @PageableDefault(size = 5, page = 0) Pageable pageable) {
    	return new ResponseEntity<>(assembler.toResource(historyMessageRepository.findAll(pageable), historyMessageResourceAssembler), HttpStatus.OK);
    }
    
    @RequestMapping(
    		method = RequestMethod.GET, 
    		value = "/api/history/search/", 
    		produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity getHistoryMessagesByUserId(@RequestParam(value = "userId") Optional<Long> userId) {
    		return new ResponseEntity(historyMessageRepository.findByUserId(userId.get().longValue()), HttpStatus.OK);
    }    
    
    @RequestMapping(
    		method = RequestMethod.GET, 
    		value = "/api/history/{historyMessageId}",
    		produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity<HistoryMessageResource> getHistoryMessageById(
    		@PathVariable("historyMessageId") Long historyMessageId) throws ResourceNotFoundException {
    	
    	return historyMessageRepository.findOneById(historyMessageId)
                .map(historyMessage -> new ResponseEntity<>(historyMessageResourceAssembler.toResource(historyMessage), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));    	
    }
            
    @RequestMapping(
    		method = RequestMethod.DELETE,
    		value = "/api/history/{historyMessageId}",
    		produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> deleteHistoryMessage(
    		@PathVariable("historyMessageId") Long historyMessageId) throws ResourceNotFoundException {
    	
    	return historyMessageRepository.findOneById(historyMessageId)
                .map(historyMessage -> {
                	LOGGER.debug("HistoryMessage deleted: {}", historyMessageId);
                	historyMessageRepository.delete(historyMessageId);
                	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));        	
    }    
    
}
