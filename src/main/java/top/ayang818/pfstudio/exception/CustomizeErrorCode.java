package top.ayang818.pfstudio.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    SUCCESS(200, "success"),
    SERVER_ERROR(404, "server-error");

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
