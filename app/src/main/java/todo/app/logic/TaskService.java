package todo.app.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import todo.app.model.Task;
import todo.app.model.TaskRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {
    private final TaskRepository repository;
    public static final Logger logger = LoggerFactory.getLogger(TaskService.class);


    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync(){
        logger.info("Supply async");
        return CompletableFuture.supplyAsync(repository::findAll);
    }
}
