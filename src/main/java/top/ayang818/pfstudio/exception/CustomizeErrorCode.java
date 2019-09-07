package top.ayang818.pfstudio.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    SUCCESS(200, "成功"),
    SERVER_ERROR(404, "服务器错误"),
    NEVER_AUTHRIZED(401, "没有权限，别搞事嗷!"),
    NO_SUCH_USER(401, "没有这个用户，别乱写嗷!"),
    NO_SUCH_COMMENT(404, "评论不存在!");

    private Integer code;
    private String messsge;

    CustomizeErrorCode(Integer code, String messsge) {
        this.code = code;
        this.messsge = messsge;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return messsge;
    }

}
