package todo.app.logic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import todo.app.TaskConfigurationProperties;
import todo.app.model.ProjectRepository;
import todo.app.model.TaskGroupRepository;
import todo.app.model.TaskRepository;

@Configuration
public class LogicConfiguration {

    @Bean
    ProjectService projectService(final ProjectRepository repository,
                           final TaskGroupRepository taskGroupRepository,
                           final TaskConfigurationProperties config){
        return new ProjectService(repository, taskGroupRepository, config);
    }

    @Bean
    TaskGroupService taskGroupService(final TaskRepository taskRepository, final TaskGroupRepository taskGroupRepository){
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }
}
