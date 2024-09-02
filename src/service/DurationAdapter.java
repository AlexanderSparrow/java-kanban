package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.nullValue();
        }else {
            out.value(value.toString());  // Сериализуем Duration как строку (например, "PT4M")
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        return Duration.parse(in.nextString());  // Десериализуем строку обратно в Duration
    }
}
