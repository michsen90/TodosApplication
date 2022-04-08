package todo.app.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import todo.app.model.TaskGroup;
import todo.app.model.TaskGroupRepository;
import todo.app.model.TaskRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskGroupServiceTest {

    @Test
    @DisplayName("should throw when undone tasks")
    void toogleGroup_undoneTasks_throwsIllegalStateException(){
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        //system under test
        var toTest = new TaskGroupService(null, mockTaskRepository);
        //when
        var exception = catchThrowable(()-> toTest.toogleGroup(1));
        //then
        assertThat(exception).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone tasks");
    }


    @Test
    @DisplayName("should throw when no group")
    void toogleGroup_wrongId_throwsIllegalArgumentException(){
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.empty());

        //system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
        //when
        var exception = catchThrowable(() -> toTest.toogleGroup(1));

        //then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should toogle group")
    void toogleGroup_worksAsExpected(){
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        var group = new TaskGroup();
        var beforeToogle = group.isDone();
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(group));

        //system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
        //when
        toTest.toogleGroup(1);
        //then
        assertThat(group.isDone()).isEqualTo(!beforeToogle);
    }

    private TaskRepository taskRepositoryReturning(final boolean result) {
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(result);
        return mockTaskRepository;
    }
}
