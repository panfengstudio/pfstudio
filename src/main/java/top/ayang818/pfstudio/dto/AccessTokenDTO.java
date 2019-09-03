package top.ayang818.pfstudio.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
