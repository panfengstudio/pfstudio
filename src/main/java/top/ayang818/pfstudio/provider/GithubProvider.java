package top.ayang818.pfstudio.provider;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import top.ayang818.pfstudio.dto.GithubAccessTokenDTO;
import top.ayang818.pfstudio.dto.GithubUserDTO;
import top.ayang818.pfstudio.util.OkHttpSingletonUtil;

import java.io.IOException;


@Component
public class GithubProvider {

    public String getAccessToken(GithubAccessTokenDTO githubAccessTokenDTO) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // 单例模式获取OkhttpClient对象
        OkHttpClient client = OkHttpSingletonUtil.getInstance();

        RequestBody body = RequestBody.create(JSON, com.alibaba.fastjson.JSON.toJSONString(githubAccessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            System.out.println("https://api.github.com/user?access_token=" +  token);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUserDTO getGithubUserDTO(String token) {

        // 单例模式获取OkhttpClient对象
        OkHttpClient client = OkHttpSingletonUtil.getInstance();

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + token)
                .build();

        try(Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            GithubUserDTO githubUserDTO = JSON.parseObject(string, GithubUserDTO.class);
            return githubUserDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
