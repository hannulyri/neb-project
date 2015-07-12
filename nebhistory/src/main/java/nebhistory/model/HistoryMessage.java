package nebhistory.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "history")
public class HistoryMessage extends BaseEntity {
	
	private static final long serialVersionUID = 1L;	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(1)
    private Long userId;

    @Size(min = 1, max = 100) 
    private String message;
    
	public HistoryMessage() {
    	
    }
	
	public HistoryMessage(Long userId, String message) {
		this.userId = userId;
		this.message = message;    	
    }	

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
