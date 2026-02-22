package io.example.wedzy.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.Task
import io.example.wedzy.data.model.TaskCategory
import io.example.wedzy.data.model.TaskPriority
import io.example.wedzy.data.model.TaskStatus
import io.example.wedzy.data.repository.TaskRepository
import io.example.wedzy.data.repository.WeddingProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null,
    val tasksGenerated: Boolean = false,
    val generatedTaskCount: Int = 0
)

enum class TaskFilter {
    ALL, PENDING, IN_PROGRESS, COMPLETED, OVERDUE
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val weddingProfileRepository: WeddingProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                _uiState.update { state ->
                    state.copy(
                        tasks = tasks,
                        filteredTasks = filterTasks(tasks, state.selectedFilter),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun setFilter(filter: TaskFilter) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredTasks = filterTasks(state.tasks, filter)
            )
        }
    }
    
    fun generateTasksFromTemplate() {
        viewModelScope.launch {
            try {
                val profile = weddingProfileRepository.getProfileOnce()
                if (profile != null && profile.weddingDate > 0) {
                    val count = taskRepository.generateTasksFromTemplate(profile.weddingDate)
                    _uiState.update { it.copy(tasksGenerated = true, generatedTaskCount = count) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to generate tasks: ${e.message}") }
            }
        }
    }
    
    fun resetGeneratedFlag() {
        _uiState.update { it.copy(tasksGenerated = false) }
    }
    
    private fun filterTasks(tasks: List<Task>, filter: TaskFilter): List<Task> {
        val now = System.currentTimeMillis()
        return when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { it.status == TaskStatus.PENDING }
            TaskFilter.IN_PROGRESS -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
            TaskFilter.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilter.OVERDUE -> tasks.filter { 
                it.dueDate != null && it.dueDate < now && it.status != TaskStatus.COMPLETED 
            }
        }
    }
    
    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.completeTask(task)
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: TaskCategory = TaskCategory.OTHER,
    val assignedTo: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateDueDate(date: Long?) {
        _uiState.update { it.copy(dueDate = date) }
    }
    
    fun updatePriority(priority: TaskPriority) {
        _uiState.update { it.copy(priority = priority) }
    }
    
    fun updateCategory(category: TaskCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun updateAssignedTo(assignedTo: String) {
        _uiState.update { it.copy(assignedTo = assignedTo) }
    }
    
    fun canSave(): Boolean = _uiState.value.title.isNotBlank()
    
    fun saveTask() {
        if (!canSave()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val task = Task(
                    title = _uiState.value.title.trim(),
                    description = _uiState.value.description.trim(),
                    dueDate = _uiState.value.dueDate,
                    priority = _uiState.value.priority,
                    category = _uiState.value.category,
                    assignedTo = _uiState.value.assignedTo.ifBlank { null }
                )
                taskRepository.insertTask(task)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()
    
    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()
    
    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _task.value = taskRepository.getTaskById(taskId)
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
            _task.value = task
        }
    }
    
    fun completeTask() {
        _task.value?.let { task ->
            viewModelScope.launch {
                taskRepository.completeTask(task)
                _task.value = task.copy(
                    status = TaskStatus.COMPLETED,
                    completedAt = System.currentTimeMillis()
                )
            }
        }
    }
    
    fun deleteTask() {
        _task.value?.let { task ->
            viewModelScope.launch {
                taskRepository.deleteTask(task)
                _isDeleted.value = true
            }
        }
    }
}
