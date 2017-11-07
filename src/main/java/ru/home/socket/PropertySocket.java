package ru.home.socket;

public class PropertySocket {

    private boolean socketEnabled = true;

    public boolean isSocketEnabled() {
        return socketEnabled;
    }

    public void setSocketEnabled(boolean socketEnabled) {
        this.socketEnabled = socketEnabled;
    }

    @Override
    public String toString() {
        return "PropertySocket{" +
                "socketEnabled=" + socketEnabled +
                '}';
    }
}
