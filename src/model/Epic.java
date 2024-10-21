package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
   public List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{id=" + id + ", name='" + name + "', description='" + description + "', status=" + status + "}";
    }
}





