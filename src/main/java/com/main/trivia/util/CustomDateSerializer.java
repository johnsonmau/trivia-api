package com.main.trivia.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CustomDateSerializer extends StdSerializer<ZonedDateTime> {

    public CustomDateSerializer() {
        this(null);
    }

    public CustomDateSerializer(Class<ZonedDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // Format with EST or other abbreviation explicitly
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

        String daySuffix = getDayOfMonthSuffix(value.getDayOfMonth());
        Date date = Date.from(value.toInstant());

        String formattedDate = String.format(formatter.format(date), daySuffix);
        gen.writeString(formattedDate);
    }

    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}

