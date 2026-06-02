package ru.smc.smc.api.application.common.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class MessageFileRequest {
    @JsonAlias("name")
    private String fileName;
    private String mimeType;
    @JsonAlias({"base64", "content"})
    private String contentBase64;
}
