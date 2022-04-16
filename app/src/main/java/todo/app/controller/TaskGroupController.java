package todo.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import todo.app.logic.TaskGroupService;
import todo.app.model.Task;
import todo.app.model.TaskRepository;
import todo.app.model.projection.GroupReadModel;
import todo.app.model.projection.GroupWriteModel;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("groups")
public class TaskGroupController {

    public static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskRepository repository;
    private final TaskGroupService service;

    public TaskGroupController(final TaskRepository taskRepository, final TaskGroupService service) {
        this.repository = taskRepository;
        this.service = service;
    }

    @PostMapping
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate){
        GroupReadModel result = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping
    ResponseEntity<List<GroupReadModel>> readAllGroups(Pageable page){
        return ResponseEntity.ok(service.readAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toogleGroup(@PathVariable int id){
        service.toogleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
