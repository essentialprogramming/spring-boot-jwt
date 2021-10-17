package code.project.springbootjwt.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import code.project.springbootjwt.repository.ApplicationUserRepository;
import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	 ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
         if (applicationUser == null) {
             throw new UsernameNotFoundException(username);
         }
         return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
}