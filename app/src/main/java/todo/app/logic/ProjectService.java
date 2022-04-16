package todo.app.logic;

import todo.app.TaskConfigurationProperties;
import todo.app.model.*;
import todo.app.model.projection.GroupReadModel;
import todo.app.model.projection.GroupTaskWriteModel;
import todo.app.model.projection.GroupWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;

    ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config, TaskGroupService taskGroupService) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.taskGroupService = taskGroupService;
    }

    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project save(final Project toSave) {
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasksFromTemplate() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        GroupReadModel result = repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(project.getSteps().stream()
                            .map(projectStep -> {
                                var task = new GroupTaskWriteModel();
                                task.setDescription(projectStep.getDescription());
                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                return task;
                            }
                            ).collect(Collectors.toSet())
                    );
                    return taskGroupService.createGroup(targetGroup);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return result;
    }
}
