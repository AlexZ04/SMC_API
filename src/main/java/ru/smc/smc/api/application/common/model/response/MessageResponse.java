package ru.smc.smc.api.application.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private List<PreviewMessage> previewMessages;
    private List<UUID> filesIds;
    private String responseText;
    private List<List<ElementModel>> inlineElements;
    private List<List<ElementModel>> replyElements;

    public static MessageResponseBuilder builder() {
        return new MessageResponseBuilder();
    }

    public static class MessageResponseBuilder {

        private final List<PreviewMessage> previewMessages = new ArrayList<>();
        private final List<UUID> filesIds = new ArrayList<>();
        private final List<List<ElementModel>> inlineElements = new ArrayList<>();
        private final List<List<ElementModel>> replyElements = new ArrayList<>();
        private String responseText;

        public MessageResponse build() {
            MessageResponse response = new MessageResponse();
            response.setPreviewMessages(this.previewMessages);
            response.setFilesIds(this.filesIds);
            response.setResponseText(this.responseText);
            response.setInlineElements(this.inlineElements);
            response.setReplyElements(this.replyElements);
            return response;
        }

        public MessageResponseBuilder responseText(String messages) {
            this.responseText = messages;
            return this;
        }

        public MessageResponseBuilder addPreviewMessage(String message) {
            this.previewMessages.add(new PreviewMessage(message, List.of()));
            return this;
        }

        public MessageResponseBuilder addPreviewMessage(String message, List<List<ElementModel>> inlineElements) {
            this.previewMessages.add(new PreviewMessage(message, inlineElements));
            return this;
        }

        public MessageResponseBuilder addPreviewMessage(PreviewMessage previewMessage) {
            this.previewMessages.add(previewMessage);
            return this;
        }

        public MessageResponseBuilder addFileId(UUID file) {
            this.filesIds.add(file);
            return this;
        }

        public MessageResponseBuilder addInlineButton(ElementModel button) {
            List<ElementModel> row = new ArrayList<>();
            row.add(button);
            this.inlineElements.add(row);
            return this;
        }

        public MessageResponseBuilder addInlineRow(ElementModel... buttons) {
            List<ElementModel> row = new ArrayList<>();
            Collections.addAll(row, buttons);
            this.inlineElements.add(row);
            return this;
        }

        public MessageResponseBuilder addInlineRow(List<ElementModel> row) {
            this.inlineElements.add(row);
            return this;
        }

        public MessageResponseBuilder addReplyButton(ElementModel button) {
            List<ElementModel> row = new ArrayList<>();
            row.add(button);
            this.replyElements.add(row);
            return this;
        }

        public MessageResponseBuilder addReplyRow(ElementModel... buttons) {
            List<ElementModel> row = new ArrayList<>();
            Collections.addAll(row, buttons);
            this.replyElements.add(row);
            return this;
        }

        public MessageResponseBuilder addReplyRow(List<ElementModel> row) {
            this.replyElements.add(row);
            return this;
        }
    }
}
