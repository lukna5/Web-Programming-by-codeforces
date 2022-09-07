package ru.itmo.wp.lesson8.form;

import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NoticeForm {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NotNull(message = "Please write something")
    @NotEmpty(message = "Please write something")
    @Lob
    @Size(min = 1, max = 255, message =
            "It is too big notification")
    private String content;

}