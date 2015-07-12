package nebhistory.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import nebhistory.controller.HistoryMessageController;
import nebhistory.model.HistoryMessage;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class HistoryMessageResourceAssembler implements ResourceAssembler<HistoryMessage, HistoryMessageResource> {
       
    @Override
    public HistoryMessageResource toResource(HistoryMessage historyMessage) {
        HistoryMessageResource resource = new HistoryMessageResource(historyMessage);

        try {
            resource.add(linkTo(methodOn(HistoryMessageController.class).getHistoryMessageById(historyMessage.getId()))
                    .withSelfRel());
        } catch (ResourceNotFoundException ex) {
            //do nothing
        }
        return resource;
    }

}
