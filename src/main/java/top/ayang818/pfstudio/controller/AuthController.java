package top.ayang818.pfstudio.controller;


import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.ayang818.pfstudio.dto.GithubAccessTokenDTO;
import top.ayang818.pfstudio.dto.GithubUserDTO;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.provider.GithubProvider;

import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client_id}")
    private String clientId;

    @Value("${github.client_secret}")
    private String clientSecret;

    @Value("${github.redirect_uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @RequestMapping(value = "/api/login/github/callback", method = RequestMethod.GET)
    public User githubAuth(@RequestParam("code") String code, @RequestParam("state")String state) {
        GithubAccessTokenDTO githubAccessTokenDTO = GithubAccessTokenDTO.builder().client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .redirect_uri(redirectUri)
                .state(state)
                .build();
        String token = githubProvider.getAccessToken(githubAccessTokenDTO);
        GithubUserDTO githubUserDTO = githubProvider.getGithubUserDTO(token);
        if (githubUserDTO != null) {
            User user = new User();
            BeanUtils.copyProperties(githubUserDTO, user);
            user.setGmtCreated(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreated());
            user.setToken(UUID.randomUUID().toString());
            userMapper.insert(user);
            return user;
        }

        return null;
    }

    @RequestMapping(value = "/api/login/qq/callback", method = RequestMethod.GET)
    public Object qqAuth(@RequestParam("code")String code) {

        return null;
    }
}
