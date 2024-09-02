package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return LocalDate.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
}