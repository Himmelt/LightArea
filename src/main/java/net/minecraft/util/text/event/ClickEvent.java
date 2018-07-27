package net.minecraft.util.text.event;

public class ClickEvent {
    private final ClickEvent.Action action;

    public ClickEvent(ClickEvent.Action action, String value) {
        this.action = action;
    }

    public ClickEvent.Action getAction() {
        return this.action;
    }

    public enum Action {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE
    }
}