package com.telecoop.telecoop.ui.conseils;

public class ConseilAction {
    private final String actionId;
    private final String buttonLabel;
    private final String contentKey;

    public ConseilAction(String actionId, String buttonLabel, String contentKey) {
        this.actionId = actionId;
        this.buttonLabel = buttonLabel;
        this.contentKey = contentKey;
    }

    public String getActionId() {
        return actionId;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getContentKey() {
        return contentKey;
    }
}
