package top.ayang818.pfstudio.exception;

public class CustomizeException extends RuntimeException implements ICustomizeErrorCode{
    private Integer code;
    private String message;

    public CustomizeException(ICustomizeErrorCode iCustomizeErrorCode) {
        this.code = iCustomizeErrorCode.getCode();
        this.message = iCustomizeErrorCode.getMessage();
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
