package pl.orange.response;

public class RestResponse {

    private boolean res;
    private int code;
    private String msg;
    private String error;

    public RestResponse() {    }

    @Override
    public String toString() {
        return res + " " + code + " " + msg + " " + error ;
    }

    public RestResponse(Boolean result,int code,String message) {
        this.res = result;
        this.code = code;
        this.msg = message;
        this.error= "empty";
    }

    public RestResponse(Boolean result,int code,String message,String error) {
        this.code = code;
        this.res = result;
        this.msg = message;
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isRes() {
        return res;
    }

    public void setRes(boolean res) {
        this.res = res;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
