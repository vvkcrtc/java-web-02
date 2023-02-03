package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class Request {
    private final String requestLine;
    private final String method;
    private final String resourcePath;
    private final String query;
    private final List<NameValuePair> queryParams;

    public Request(String requestLine) {
        this.requestLine = requestLine;
        String[] requestLineParts = requestLine.split(" ");
        this.method = requestLineParts[0];

        var uri = URI.create(requestLineParts[1]);
        this.resourcePath = uri.getPath();
        this.query = uri.getQuery();
        this.queryParams = URLEncodedUtils.parse(uri, "UTF-8");

    }
    public String getMethod() {
        return method;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String name) {
        return queryParams.stream()
                .filter(p -> p.getName().equals(name))
                .map(p -> p.getValue())
                .findFirst().get();
    }

}
