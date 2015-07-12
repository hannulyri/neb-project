package nebhistory.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class ValidationError {
    @JsonProperty("Errors")
    private final ImmutableList<String> errors;
    @JsonProperty("Field")
    private final String field;

    private ValidationError(String field, String... errors) {
        this.field = checkNotNull(field);
        this.errors = ImmutableList.copyOf(errors);
    }

    public static List<ValidationError> of(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(f -> new
                ValidationError(f.getField(), f.getDefaultMessage())).collect(Collectors.toList());
    }

    public String getField() {
        return field;
    }

    public ImmutableList<String> getErrors() {
        return errors;
    }
}