package nebhistory.dto;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.List;

public class HistoryMessageDTO {

    @NotNull
    @Min(1)
    private Long userId;

    @Size(min = 1, max = 100) 
    private String message;

    public HistoryMessageDTO() {
    }

    public HistoryMessageDTO(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
