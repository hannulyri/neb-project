package nebusers.resource;

import nebusers.model.User;

import org.springframework.hateoas.Resource;

public class UserResource extends Resource<User> {

    public UserResource(User user) {
        super(user);
    }

}
