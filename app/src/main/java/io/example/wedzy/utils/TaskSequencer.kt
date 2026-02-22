package io.example.wedzy.utils

import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.TaskDependency
import io.example.wedzy.data.model.DependencyType
import io.example.wedzy.data.model.TaskStatus
import java.util.Calendar

object TaskSequencer {
    
    data class TaskWithDependencies(
        val task: Task,
        val dependencies: List<TaskDependency>,
        val dependentTasks: List<Task>,
        val isBlocked: Boolean,
        val blockingTasks: List<Task>,
        val suggestedStartDate: Long?
    )
    
    fun analyzeTaskDependencies(
        task: Task,
        allTasks: List<Task>,
        allDependencies: List<TaskDependency>
    ): TaskWithDependencies {
        val dependencies = allDependencies.filter { it.taskId == task.id }
        val dependentTaskIds = allDependencies.filter { it.dependsOnTaskId == task.id }.map { it.taskId }
        val dependentTasks = allTasks.filter { it.id in dependentTaskIds }
        
        val blockingDependencies = dependencies.filter { it.dependencyType == DependencyType.BLOCKS }
        val blockingTaskIds = blockingDependencies.map { it.dependsOnTaskId }
        val blockingTasks = allTasks.filter { it.id in blockingTaskIds && it.status != TaskStatus.COMPLETED }
        
        val isBlocked = blockingTasks.isNotEmpty()
        
        val suggestedStartDate = calculateSuggestedStartDate(task, dependencies, allTasks)
        
        return TaskWithDependencies(
            task = task,
            dependencies = dependencies,
            dependentTasks = dependentTasks,
            isBlocked = isBlocked,
            blockingTasks = blockingTasks,
            suggestedStartDate = suggestedStartDate
        )
    }
    
    fun canCompleteTask(
        task: Task,
        allTasks: List<Task>,
        allDependencies: List<TaskDependency>
    ): Pair<Boolean, String?> {
        val blockingDependencies = allDependencies.filter { 
            it.taskId == task.id && it.dependencyType == DependencyType.BLOCKS 
        }
        
        val incompleteDependencies = blockingDependencies.filter { dep ->
            val dependencyTask = allTasks.find { it.id == dep.dependsOnTaskId }
            dependencyTask?.status != TaskStatus.COMPLETED
        }
        
        return if (incompleteDependencies.isEmpty()) {
            true to null
        } else {
            val blockingTaskNames = incompleteDependencies.mapNotNull { dep ->
                allTasks.find { it.id == dep.dependsOnTaskId }?.title
            }
            false to "Complete these tasks first: ${blockingTaskNames.joinToString(", ")}"
        }
    }
    
    fun getTasksUnlockedByCompletion(
        completedTask: Task,
        allTasks: List<Task>,
        allDependencies: List<TaskDependency>
    ): List<Task> {
        val dependentTaskIds = allDependencies
            .filter { it.dependsOnTaskId == completedTask.id && it.dependencyType == DependencyType.BLOCKS }
            .map { it.taskId }
        
        return allTasks.filter { task ->
            task.id in dependentTaskIds && task.status == TaskStatus.PENDING
        }.filter { task ->
            val (canComplete, _) = canCompleteTask(task, allTasks, allDependencies)
            canComplete
        }
    }
    
    fun sortTasksByDependencies(
        tasks: List<Task>,
        dependencies: List<TaskDependency>
    ): List<Task> {
        val taskMap = tasks.associateBy { it.id }
        val sorted = mutableListOf<Task>()
        val visited = mutableSetOf<Long>()
        
        fun visit(taskId: Long) {
            if (taskId in visited) return
            visited.add(taskId)
            
            val taskDependencies = dependencies.filter { it.taskId == taskId }
            taskDependencies.forEach { dep ->
                visit(dep.dependsOnTaskId)
            }
            
            taskMap[taskId]?.let { sorted.add(it) }
        }
        
        tasks.forEach { visit(it.id) }
        
        return sorted
    }
    
    private fun calculateSuggestedStartDate(
        task: Task,
        dependencies: List<TaskDependency>,
        allTasks: List<Task>
    ): Long? {
        if (dependencies.isEmpty()) return null
        
        val latestDependencyDate = dependencies.mapNotNull { dep ->
            val dependencyTask = allTasks.find { it.id == dep.dependsOnTaskId }
            dependencyTask?.dueDate?.let { dueDate ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dueDate
                calendar.add(Calendar.DAY_OF_MONTH, dep.delayDays)
                calendar.timeInMillis
            }
        }.maxOrNull()
        
        return latestDependencyDate
    }
    
    fun generateDependencyChain(
        task: Task,
        allTasks: List<Task>,
        allDependencies: List<TaskDependency>
    ): List<Task> {
        val chain = mutableListOf<Task>()
        val visited = mutableSetOf<Long>()
        
        fun buildChain(taskId: Long) {
            if (taskId in visited) return
            visited.add(taskId)
            
            val dependencies = allDependencies.filter { it.taskId == taskId }
            dependencies.forEach { dep ->
                buildChain(dep.dependsOnTaskId)
            }
            
            allTasks.find { it.id == taskId }?.let { chain.add(it) }
        }
        
        buildChain(task.id)
        return chain
    }
}
