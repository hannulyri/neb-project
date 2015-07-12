package nebhistory.resource;

import nebhistory.model.HistoryMessage;

import org.springframework.hateoas.Resource;

public class HistoryMessageResource extends Resource<HistoryMessage> {

    public HistoryMessageResource(HistoryMessage historyMessage) {
        super(historyMessage);
    }

}
