package todo.app.model.projection;

import todo.app.model.Project;
import todo.app.model.TaskGroup;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupWriteModel {

    private String description;
    private Set<GroupTaskWriteModel> tasks;

    String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    Set<GroupTaskWriteModel> getTasks() {
        return tasks;
    }

    public void setTasks(Set<GroupTaskWriteModel> tasks) {
        this.tasks = tasks;
    }

    public TaskGroup toGroup(Project project){
        var result = new TaskGroup();
        result.setDescription(description);
        result.setTasks(tasks.stream()
                .map(source -> source.toTask(result))
                .collect(Collectors.toSet())
        );
        result.setProject(project);
        return result;
    }
}
