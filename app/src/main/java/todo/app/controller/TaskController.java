package todo.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import todo.app.model.Task;

import todo.app.model.TaskRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class TaskController {

    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;

    public TaskController(final TaskRepository taskRepository) {
        this.repository = taskRepository;
    }

    @GetMapping(value = "tasks", params = {"!page", "!size", "!sort"})
    ResponseEntity<List<Task>> readAllTasks(){
        logger.warn("Exposing all the tasks !!!");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.warn("Exposing all the tasks !!!");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping("tasks/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id){
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("tasks")
    ResponseEntity<Task> createTask(@RequestBody @Valid Task task){
        Task result = repository.save(task);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @PutMapping("tasks/{id}")
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
    @PatchMapping("tasks/{id}")
    public ResponseEntity<?> toogleTask(@PathVariable int id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }

}
