package cpw.mods.fml.common.eventhandler;

public class Event {

    public boolean isCancelable() {
        return false;
    }

    public boolean isCanceled() {
        return false;
    }

    public void setCanceled(boolean cancel) {
    }
}
