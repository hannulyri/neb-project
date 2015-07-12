package nebusers.resource;

import nebusers.dto.HistoryMessageDTO;

import org.springframework.hateoas.Resource;

public class HistoryMessageResource extends Resource<HistoryMessageDTO> {

    public HistoryMessageResource(HistoryMessageDTO historyMessageDTO) {
        super(historyMessageDTO);
    }

}
