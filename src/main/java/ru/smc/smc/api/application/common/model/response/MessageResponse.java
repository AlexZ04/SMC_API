package ru.smc.smc.api.application.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private List<String> previewMessages;
    private String responseText;
    private List<List<ElementModel>> inlineElements;
    private List<List<ElementModel>> replyElements;

    public static MessageResponseBuilder builder() {
        return new MessageResponseBuilder();
    }

    public static class MessageResponseBuilder {

        private final List<String> previewMessages = new ArrayList<>();
        private final List<List<ElementModel>> inlineElements = new ArrayList<>();
        private final List<List<ElementModel>> replyElements = new ArrayList<>();
        private String responseText;

        public MessageResponse build() {
            MessageResponse response = new MessageResponse();
            response.setPreviewMessages(this.previewMessages);
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
            this.previewMessages.add(message);
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
    }
}
