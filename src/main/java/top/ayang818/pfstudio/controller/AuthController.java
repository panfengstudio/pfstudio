package top.ayang818.pfstudio.controller;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.ayang818.pfstudio.dto.GithubAccessTokenDTO;
import top.ayang818.pfstudio.dto.GithubUserDTO;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.model.UserExample;
import top.ayang818.pfstudio.provider.GithubProvider;
import top.ayang818.pfstudio.service.UserService;
import top.ayang818.pfstudio.util.AliCloudOssServeUtil;
import top.ayang818.pfstudio.util.OkHttpSingletonUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client_id}")
    private String clientId;

    @Value("${github.client_secret}")
    private String clientSecret;

    @Value("${github.redirect_uri}")
    private String redirectUri;

    @Value("${url.frontend.domain}")
    private String domain;

    @Value("${ali.ossdir}")
    private String filedir;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/githubcallback")
    public String githubAuth(@RequestParam("code") String code, @RequestParam("state") String state, Model model) {
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
                User user = verifyUser.get(0);
                model.addAttribute("token", user.getToken());
                model.addAttribute("domain", domain);
                return "githubcallback";
            }
            // 第一次登陆，将Github的头像存到阿里云
            OkHttpClient okHttpClient = OkHttpSingletonUtil.getInstance();
            Request request = new Request.Builder()
                    //.url(githubUserDTO.getAvatarUrl())
                    .url(githubUserDTO.getAvatarUrl())
                    .build();

            String avatarUrl = null;
            try (Response response = okHttpClient.newCall(request).execute()) {
                AliCloudOssServeUtil ossServeUtil = AliCloudOssServeUtil.getInstance();
                assert response.body() != null;
                InputStream inputStream = response.body().byteStream();
                ossServeUtil.uploadImageToOss(inputStream, filedir+githubUserDTO.getId());
                avatarUrl = ossServeUtil.getUrl(filedir + githubUserDTO.getId() + ".png").split("[?]")[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
            User user = new User();
            user.setName(githubUserDTO.getName());
            user.setAvatarUrl(avatarUrl != null ? avatarUrl : githubUserDTO.getAvatarUrl());
            user.setBio(githubUserDTO.getBio());
            user.setGmtCreated(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreated());
            user.setToken(UUID.randomUUID().toString());
            user.setGithubId(githubUserDTO.getId());
            userService.insertOrUpdate(user);
            model.addAttribute("token", user.getToken());
            model.addAttribute("domain", domain);
            return "githubcallback";
        }

        return null;
    }

    @GetMapping("qqcallback")
    public Object qqAuth(@RequestParam("code") String code) {

        return null;
    }


}
