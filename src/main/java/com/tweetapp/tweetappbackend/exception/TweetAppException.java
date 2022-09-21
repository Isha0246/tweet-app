package com.tweetapp.tweetappbackend.exception;

public class TweetAppException extends Exception {
    private int errorId;

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public TweetAppException(int errorId) {
        this.errorId = errorId;
    }

    public TweetAppException(String message, int errorId) {
        super(message);
        this.errorId = errorId;
    }

    public TweetAppException(Throwable cause, int errorId) {
        super(cause);
        this.errorId = errorId;
    }
}
