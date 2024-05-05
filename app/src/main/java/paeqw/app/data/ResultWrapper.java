package paeqw.app.data;
/**
*   Helper class to hold the result
* */

public class ResultWrapper<T> {
    private Result<T> result;

    public synchronized void setResult(Result<T> result) {
        this.result = result;
    }

    public synchronized Result<T> getResult() {
        return result;
    }
}
