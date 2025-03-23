package com.main.trivia.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomDateSerializer extends StdSerializer<ZonedDateTime> {

    private static final Logger LOGGER = Logger.getLogger(CustomDateSerializer.class.getName());

    public CustomDateSerializer() {
        this(null);
    }

    public CustomDateSerializer(Class<ZonedDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // Default to system timezone if no request context is available
        String userTimezone = ZoneId.systemDefault().getId();

        // Try to get the timezone from the request header
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String headerTimezone = attributes.getRequest().getHeader("X-User-Timezone");
                if (headerTimezone != null && !headerTimezone.isEmpty()) {
                    userTimezone = headerTimezone; // Use the client-provided timezone
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not retrieve timezone from request", e);
        }

        // Use the user's timezone (or fallback) in the formatter
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH)
                .withZone(ZoneId.of(userTimezone));

        String formattedDate = value.format(formatter);

        gen.writeString(formattedDate);
    }
}
