package todo.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import todo.app.logic.TaskService;
import todo.app.model.Task;

import todo.app.model.TaskRepository;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("tasks")
public class TaskController {

    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;
    private final TaskService service;

    public TaskController(final TaskRepository taskRepository, TaskService service) {
        this.repository = taskRepository;
        this.service = service;
    }

    /**@GetMapping(params = {"!page", "!size", "!sort"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks(){
        logger.warn("Exposing all the tasks !!!");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }**/

    @GetMapping(params = {"!page", "!size", "!sort"})
    ResponseEntity<List<Task>> readAllTasks(){
        logger.warn("Exposing all the tasks !!!");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.warn("Exposing all the tasks !!!");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }


    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody @Valid Task task){
        Task result = repository.save(task);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody Task toUpdate){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task -> {
            task.updateFrom(toUpdate);
            repository.save(task);
        });
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toogleTask(@PathVariable int id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }

}
