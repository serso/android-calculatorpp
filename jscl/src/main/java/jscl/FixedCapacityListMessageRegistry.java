package jscl;

import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageRegistry;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;

@NotThreadSafe
public class FixedCapacityListMessageRegistry implements MessageRegistry {

    @Nonnull
    private final List<Message> messages;

    private final int capacity;

    private volatile int size;

    public FixedCapacityListMessageRegistry(int capacity) {
        this.size = 0;
        this.capacity = capacity;
        this.messages = new ArrayList<Message>(capacity);
    }

    public void addMessage(@Nonnull Message message) {
        if (!this.messages.contains(message)) {
            if (this.size <= this.capacity) {
                this.messages.add(message);
                this.size++;
            } else {
                this.messages.remove(0);
                this.messages.add(message);
            }
        }
    }

    @Nonnull
    public Message getMessage() {
        if (hasMessage()) {
            this.size--;
            return messages.remove(0);
        } else {
            throw new IllegalStateException("No messages!");
        }
    }

    public boolean hasMessage() {
        return size > 0;
    }
}
