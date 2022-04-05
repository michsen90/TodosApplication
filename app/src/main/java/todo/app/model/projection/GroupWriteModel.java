package todo.app.model.projection;

import todo.app.model.TaskGroup;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupWriteModel {

    private String description;
    private Set<GroupTaskWriteModel> tasks;

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    Set<GroupTaskWriteModel> getTasks() {
        return tasks;
    }

    void setTasks(Set<GroupTaskWriteModel> tasks) {
        this.tasks = tasks;
    }

    public TaskGroup toGroup(){
        var result = new TaskGroup();
        result.setDescription(description);
        result.setTasks(tasks.stream()
                .map(GroupTaskWriteModel::toTask)
                .collect(Collectors.toSet())
        );
        return result;
    }
}
