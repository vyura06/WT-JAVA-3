package app.connection;

import app.enums.MsgType;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {
    private final MsgType type;
    private final Object[] args;

    public Message(MsgType type) {
        this(type, new Object[]{});
    }

    public Message(MsgType type, Object[] args) {
        this.type = Objects.requireNonNull(type);
        this.args = args;
    }

    public MsgType getType() {
        return type;
    }

    public Object[] getArgs() {
        return args;
    }
}
