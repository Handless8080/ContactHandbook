package controllers;

public enum  Urls {
    add("add"),
    filters("filters"),
    save("save"),
    edit("edit"),
    download("download"),
    delete("delete"),
    emails("emails"),
    sendMessages("send-messages");

    private final String value;

    public String getValue() {
        return value;
    }

    Urls(String value) {
        this.value = value;
    }
}