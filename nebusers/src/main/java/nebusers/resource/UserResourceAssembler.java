package nebusers.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Optional;

import nebusers.controller.UserController;
import nebusers.model.User;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class UserResourceAssembler implements ResourceAssembler<User, UserResource> {
       
    @Override
    public UserResource toResource(User user) {
        UserResource resource = new UserResource(user);
        
        try {
            resource.add(linkTo(methodOn(UserController.class).getUserById(user.getId()))
                    .withSelfRel());     
        } catch (ResourceNotFoundException ex) {
            //do nothing
        } catch (Exception e) {
            //do nothing
        }
        return resource;
    }

}
