package top.ayang818.pfstudio.controller;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.ayang818.pfstudio.dto.GithubAccessTokenDTO;
import top.ayang818.pfstudio.dto.GithubUserDTO;
import top.ayang818.pfstudio.exception.CustomizeErrorCode;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.model.UserExample;
import top.ayang818.pfstudio.provider.GithubProvider;
import top.ayang818.pfstudio.service.UserService;
import top.ayang818.pfstudio.util.AliCloudOssServeUtil;
import top.ayang818.pfstudio.util.OkHttpSingletonUtil;

import java.io.IOException;
import java.io.InputStream;
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

    @Value("${url.frontend.domain}")
    private String domain;

    @Value("${ali.ossdir}")
    private String filedir;

    @Value("${ali.ossendpoint}") String endpoint;
    @Value("${ali.ossaccesskeyid}") String accessKeyId;
    @Value("${ali.ossaccesskeysecret}") String accessKeySecret;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/githubcallback", method = RequestMethod.GET)
    public Object githubAuth(@RequestParam("code") String code, @RequestParam("state") String state) {
        GithubAccessTokenDTO githubAccessTokenDTO = GithubAccessTokenDTO.builder().client_id(clientId)
                .client_secret(clientSecret)
                .code(code)
                .redirect_uri(redirectUri)
                .state(state)
                .build();
        String token = githubProvider.getAccessToken(githubAccessTokenDTO);
        if ("bad_verification_code".equals(token)) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        GithubUserDTO githubUserDTO = githubProvider.getGithubUserDTO(token);
        List<User> verifyUser;
        User insertedUserOrUpdateUser;
        // 已登录过，直接返回数据库中内容
        if (githubUserDTO != null) {
            System.out.println(githubUserDTO.getName());
            UserExample userExample = new UserExample();
            userExample.createCriteria().andGithubIdEqualTo(githubUserDTO.getId());
            verifyUser = userMapper.selectByExample(userExample);
            if (verifyUser.size() != 0 && !verifyUser.get(0).getAvatarUrl().contains("github")) {
                return verifyUser.get(0).getToken();
            }
            // 第一次登陆，或头像源还是github的用户，将Github的头像存到阿里云
            OkHttpClient okHttpClient = OkHttpSingletonUtil.getInstance();
            Request request = new Request.Builder()
                    .url(githubUserDTO.getAvatarUrl())
                    .build();

            String avatarUrl = null;
            try (Response response = okHttpClient.newCall(request).execute()) {
                AliCloudOssServeUtil ossServeUtil = AliCloudOssServeUtil.getInstance(endpoint, accessKeyId, accessKeySecret);
                assert response.body() != null;
                InputStream inputStream = response.body().byteStream();
                ossServeUtil.uploadImageToOss(inputStream, filedir + githubUserDTO.getId());
                avatarUrl = ossServeUtil.getUrl(filedir + githubUserDTO.getId() + ".png").split("[?]")[0];
                System.out.println("avatarurl is "+ avatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (verifyUser.size() != 0) {
                insertedUserOrUpdateUser = verifyUser.get(0);
                insertedUserOrUpdateUser.setGmtModified(System.currentTimeMillis());
                insertedUserOrUpdateUser.setAvatarUrl(avatarUrl != null ? avatarUrl : githubUserDTO.getAvatarUrl());
                userService.insertOrUpdate(insertedUserOrUpdateUser);
                return insertedUserOrUpdateUser.getToken();
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
            return user.getToken();
        }

        return CustomizeErrorCode.SERVER_ERROR;
    }

    @RequestMapping(value = "/api/qqcallback", method = RequestMethod.GET)
    public Object qqAuth(@RequestParam("code") String code) {

        return null;
    }


}
