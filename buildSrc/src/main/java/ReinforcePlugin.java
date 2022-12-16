import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import java.util.SortedMap;

@NonNullApi
public class ReinforcePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("<------------ Reinforce Plugin ------------>");
        TaskContainer tasks = project.getTasks();
        System.out.println("Tasks Size: " + tasks.size());
        //
        TaskProvider<UnzipTask> reinforceUnzipProvider = tasks.register("reinforceUnzip", UnzipTask.class);
        UnzipTask unzipTask = reinforceUnzipProvider.get();
        unzipTask.setEnabled(true);
        unzipTask.setGroup("reinforce");
        System.out.println("Tasks Size: " + tasks.size());
        //
        SortedMap<String, Task> tasksAsMap = tasks.getAsMap();
        System.out.println("Task Map: " + tasksAsMap);
        System.out.println("Task Map Size: " + tasksAsMap.size());
        //
        Task clean = tasks.findByPath("clean");
        System.out.println(clean);
        if (clean != null) {
            clean.dependsOn(unzipTask);
        }
        System.out.println("<------------ Reinforce Plugin ------------>");
    }
}
