package top.ayang818.pfstudio.dto;


import lombok.Data;

@Data
public class GithubUserDTO {
    private Integer id;
    private String avatarUrl;
    private String name;
    private String bio;
    private Long gmtCreated;
    private Long gmtModified;
}
