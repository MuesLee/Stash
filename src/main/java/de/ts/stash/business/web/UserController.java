package de.ts.stash.business.web;

import static de.ts.stash.security.SecurityConstants.HEADER_STRING;
import static de.ts.stash.security.SecurityConstants.TOKEN_PREFIX;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ts.stash.auth.user.ApplicationUser;
import de.ts.stash.auth.user.UserRepository;
import de.ts.stash.security.AuthTokenProvider;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	AuthTokenProvider authTokenProvider;

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository applicationUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody ApplicationUser user, HttpServletResponse response) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        ApplicationUser createdUser = userRepository.save(user);
        String token = authTokenProvider.provideAuthToken(createdUser);
        
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        response.setStatus(HttpStatus.CREATED.value());
    }
}