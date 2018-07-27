package net.minecraft.event;

public class ClickEvent {
    private final ClickEvent.Action field_150671_a;

    public ClickEvent(ClickEvent.Action action, String value) {
        this.field_150671_a = action;
    }

    public ClickEvent.Action func_150669_a() {
        return this.field_150671_a;
    }

    public enum Action {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        TWITCH_USER_INFO,
        SUGGEST_COMMAND
    }
}
