package com.volvo.wis.pbv.viewmodels;

public class OperationResult {

    private boolean Success;
    private int Code;
    private String Message;

    public OperationResult() { }

    public OperationResult(boolean success) {
        this.Success = success;
        this.Code = !success ? 500 : 200;
    }

    public OperationResult(boolean success, String message) {
        this.Success = success;
        this.Code = !success ? 500 : 200;
        this.Message = message;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}