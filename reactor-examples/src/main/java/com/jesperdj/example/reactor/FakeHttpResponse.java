package com.jesperdj.example.reactor;

public class FakeHttpResponse {

    private final String url;
    private final int statusCode;

    public FakeHttpResponse(String url, int statusCode) {
        this.url = url;
        this.statusCode = statusCode;
    }

    public String getUrl() {
        return url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return statusCode + " " + url;
    }
}
