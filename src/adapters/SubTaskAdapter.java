package adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.SubTask;

import java.lang.reflect.Type;

public class SubTaskAdapter implements JsonSerializer<SubTask> {
    @Override
    public JsonElement serialize(SubTask subTask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", subTask.getId());
        jsonObject.addProperty("name", subTask.getName());
        jsonObject.addProperty("description", subTask.getDescription());
        jsonObject.addProperty("status", subTask.getStatus().toString());
        jsonObject.addProperty("duration", subTask.getDuration().toString());
        jsonObject.addProperty("startTime", subTask.getStartTime().toString());
        jsonObject.addProperty("epicIdTemp", subTask.getEpicIdTemp());  // Добавляем только epicId
        return jsonObject;
    }
}
