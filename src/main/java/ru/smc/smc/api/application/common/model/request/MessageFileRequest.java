package ru.smc.smc.api.application.common.model.request;

import lombok.Data;

@Data
public class MessageFileRequest {
    private String fileName;
    private String mimeType;
    private String contentBase64;
}
