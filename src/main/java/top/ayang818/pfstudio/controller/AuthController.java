package top.ayang818.pfstudio.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.ayang818.pfstudio.dto.AccessTokenDTO;
import top.ayang818.pfstudio.dto.GithubUserDTO;
import top.ayang818.pfstudio.provider.GithubProvider;

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


    @RequestMapping(value = "/api/login/github/callback", method = RequestMethod.GET)
    public Object githubAuth(@RequestParam("code") String code, @RequestParam("state")String state) {
        AccessTokenDTO accessTokenDTO = AccessTokenDTO.builder().client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .redirect_uri(redirectUri)
                .state(state)
                .build();
        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUserDTO githubUserDTO = githubProvider.getGithubUserDTO(token);

        return null;
    }
}
