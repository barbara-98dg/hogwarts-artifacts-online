package edu.tcu.cs.hogwarts_artifacts_online.security;

import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.HogwartsUser;
import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.MyUserPrincipal;
import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.converter.UserToUserDtoConverter;
import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user info.
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

        // Create a JWT.
        String token = this.jwtProvider.createToken(authentication);
        Map<String, Object> loginResultMap = new HashMap<>();

        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
