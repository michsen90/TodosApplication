package todo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import todo.app.TaskConfigurationProperties;

@RestController
class InfoController {

    private DataSourceProperties dataSourceProperties;
    private TaskConfigurationProperties myProp;

    InfoController(final DataSourceProperties dataSourceProperties, final TaskConfigurationProperties myProp) {
        this.dataSourceProperties = dataSourceProperties;
        this.myProp = myProp;
    }

    @GetMapping("info/url")
    String url(){
        return dataSourceProperties.getUrl();
    }

    @GetMapping("info/prop")
    boolean myProp(){
        return myProp.getTemplate().isAllowMultipleTasksFromTemplate();
    }

}
