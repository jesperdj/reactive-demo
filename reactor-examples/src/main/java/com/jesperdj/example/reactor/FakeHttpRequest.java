package com.jesperdj.example.reactor;

public class FakeHttpRequest {

    private final String url;

    public FakeHttpRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }
}
