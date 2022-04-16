package todo.app.logic;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import todo.app.TaskConfigurationProperties;

import todo.app.model.*;
import todo.app.model.projection.GroupReadModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalArgumentException when configuration ok and no projects for given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, null, mockConfig, null);
        //when

        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }


    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone group exists")
    void createGroup_noMultipleGroupsConfig_And_undoneGroup_throwsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //system under test
        var toTest = new ProjectService(null, mockGroupRepository, mockConfig, null);
        //when

        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone group");


    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configuration ok and no projects for given id")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroup_noProjects_throwsIllegalArgumentException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);

        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, mockGroupRepository, mockConfig, null);
        //when

        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }

    @Test
    @DisplayName("should create new group from project")
    void createGroup_configurationOk_existingProject_createsAndSavesGroup(){

        //given
        var today = LocalDate.now().atStartOfDay();
        var mockRepository = mock(ProjectRepository.class);
        //and
        var project = projectWith("bar", Set.of(-1, -2));
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));

        InMemoryGroupRepository inMemoryGroupRepo = inMemortyGroupRepository();
        var serviceWithInMemRepo = dummyGroupService(inMemoryGroupRepo);

        //and
        TaskGroupRepository inMemoryRepository = inMemortyGroupRepository();
        var countBeforeCall = inMemortyGroupRepository().count();
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, inMemoryRepository, mockConfig,serviceWithInMemRepo );
        //when
        GroupReadModel result = toTest.createGroup(today, 1);

        //then
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
        assertThat(countBeforeCall).isEqualTo(inMemortyGroupRepository().count());

    }

    private TaskGroupService dummyGroupService(InMemoryGroupRepository inMemoryGroupRepo) {
        return new TaskGroupService(inMemoryGroupRepo, null);
    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadline){

        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        var steps =  daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        when(result.getSteps()).thenReturn(steps);
        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(final boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }


    private TaskConfigurationProperties configurationReturning(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasksFromTemplate()).thenReturn(result);
        var mockConfig = mock(TaskConfigurationProperties.class);
        when((mockConfig.getTemplate())).thenReturn(mockTemplate);
        return mockConfig;
    }

    private InMemoryGroupRepository inMemortyGroupRepository(){ return new InMemoryGroupRepository(); }

    private static class InMemoryGroupRepository implements TaskGroupRepository {

        private Map<Integer, TaskGroup> map = new HashMap<>();
        private int index = 0;

        public int count(){
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(TaskGroup entity) {
            if(entity.getId() == 0){
                try {
                    //reflection
                    var field = TaskGroup.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);

                }catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
            return map.values().stream()
                    .filter(taskGroup -> !taskGroup.isDone())
                    .anyMatch(taskGroup -> taskGroup.getProject() != null && taskGroup.getProject().getId() == projectId);
        }
    }

}