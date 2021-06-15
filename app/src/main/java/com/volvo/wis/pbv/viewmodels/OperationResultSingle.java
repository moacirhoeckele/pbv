package com.volvo.wis.pbv.viewmodels;

public class OperationResultSingle<T> extends OperationResult {
    private T Data;

    public OperationResultSingle(boolean success)
    {
        super(success);
    }

    public OperationResultSingle(boolean success, T data)
    {
        super(success);
        this.Data = data;
    }

    public OperationResultSingle(boolean success, T data, String message)
    {
        super(success, message);
        this.Data = data;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
