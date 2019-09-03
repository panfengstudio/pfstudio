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
import top.ayang818.pfstudio.model.UserExample;
import top.ayang818.pfstudio.provider.GithubProvider;

import java.util.List;
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
    public User githubAuth(@RequestParam("code") String code, @RequestParam("state") String state) {
        GithubAccessTokenDTO githubAccessTokenDTO = GithubAccessTokenDTO.builder().client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .redirect_uri(redirectUri)
                .state(state)
                .build();
        String token = githubProvider.getAccessToken(githubAccessTokenDTO);
        GithubUserDTO githubUserDTO = githubProvider.getGithubUserDTO(token);

        if (githubUserDTO != null) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().andGithubIdEqualTo(githubUserDTO.getId());
            List<User> verifyUser = userMapper.selectByExample(userExample);
            if (verifyUser.size() != 0) {
                return verifyUser.get(0);
            }
            User user = new User();
            user.setName(githubUserDTO.getName());
            user.setAvatarUrl(githubUserDTO.getAvatarUrl());
            user.setBio(githubUserDTO.getBio());
            user.setGmtCreated(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreated());
            user.setToken(UUID.randomUUID().toString());
            user.setGithubId(githubUserDTO.getId());
            userMapper.insert(user);
            return user;
        }

        return null;
    }

    @RequestMapping(value = "/api/login/qq/callback", method = RequestMethod.GET)
    public Object qqAuth(@RequestParam("code") String code) {

        return null;
    }
}
