import java.awt.Color;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Property;

/**
 * This is to display debug log lines gray.
 */
public final class GrayConsoleAppender extends AbstractAppender {

    public GrayConsoleAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout
    ) {
        super(name, filter, layout, true, null);
    }

    @Override
    public void append(LogEvent event) {
        Layout<? extends Serializable> layout = getLayout();

        String text;
        if (layout == null) {
            text = event.getMessage().getFormattedMessage();
        } else {
            Serializable serializable = layout.toSerializable(event);
            if (serializable instanceof String string) {
                text = string;
            } else if (serializable instanceof byte[] bytes) {
                text = new String(bytes, StandardCharsets.UTF_8);
            } else {
                text = String.valueOf(serializable);
            }
        }

        Color previousColor = ConsoleItemPrintStream.getColorAndSet(System.out, Color.GRAY);

        try {
         	System.out.print(text);
        } finally {
           ConsoleItemPrintStream.setColor(System.out, previousColor);
        }
    }
}
