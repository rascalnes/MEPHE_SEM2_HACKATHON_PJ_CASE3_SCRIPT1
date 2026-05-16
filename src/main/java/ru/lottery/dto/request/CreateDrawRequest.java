package ru.lottery.dto.request;

public class CreateDrawRequest {
    private String name;

    public CreateDrawRequest() {}

    public CreateDrawRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}