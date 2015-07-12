package nebusers.resource;

import nebusers.dto.HistoryMessageDTO;

import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class HistoryMessageResourceAssembler implements ResourceAssembler<HistoryMessageDTO, HistoryMessageResource> {
       
    @Override
    public HistoryMessageResource toResource(HistoryMessageDTO historyMessageDTO) {
        HistoryMessageResource resource = new HistoryMessageResource(historyMessageDTO);

        return resource;
    }

}
