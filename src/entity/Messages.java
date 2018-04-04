package entity;

import java.util.ArrayList;

public class Messages {
    private ArrayList<Message> messages = new ArrayList<>();
    private Integer nRoom;
    private Integer nPrivate;

    public Messages() {
    }

    public Messages(ArrayList<Message> messages, Integer nRoom, Integer nPrivate) {
        this.messages = messages;
        this.nRoom = nRoom;
        this.nPrivate = nPrivate;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public Integer getnRoom() {
        return nRoom;
    }

    public void setnRoom(Integer nRoom) {
        this.nRoom = nRoom;
    }

    public Integer getnPrivate() {
        return nPrivate;
    }

    public void setnPrivate(Integer nPrivate) {
        this.nPrivate = nPrivate;
    }
}
