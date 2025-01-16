package com.ntloc.demo.todo;

import com.ntloc.demo.AbstractTestcontainersTest;
import com.ntloc.demo.model.Task;
import com.ntloc.demo.repo.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest extends AbstractTestcontainersTest {
    @Autowired
    TaskRepository underTest;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findTop5ByIsCompletedFalseOrderByCreatedAtDesc() {
    }
    @Test
    @Transactional
    @Rollback
    void shouldFindTop5UncompletedTasksOrderedByCreatedAtDesc() {
        // given
        for (int i = 1; i <= 10; i++) {
            underTest.save(Task.builder()
                    .title("Task Title " + i)
                    .description("Task Description " + i)
                    .isCompleted(false)
                    .createdAt(LocalDateTime.now().minusMinutes(i))
                    .build());
        }

        // when
        List<Task> result = underTest.findTop5ByIsCompletedFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(5);
        assertThat(result).extracting(Task::getTitle).containsExactly("Task Title 10", "Task Title 9", "Task Title 8", "Task Title 7", "Task Title 6");
    }
    @Test
    void shouldReturnEmptyListWhenNoUncompletedTasks() {
        // given
        for (int i = 1; i <= 5; i++) {
            underTest.save(Task.builder()
                    .title("Task Title " + i)
                    .description("Task Description " + i)
                    .isCompleted(true)
                    .createdAt(LocalDateTime.now().minusMinutes(i))
                    .build());
        }

        // when
        List<Task> result = underTest.findTop5ByIsCompletedFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).isEmpty();
    }
    @Test
    void shouldReturnAllUncompletedTasksIfLessThanFive() {
        // given
        for (int i = 1; i <= 3; i++) {
            underTest.save(Task.builder()
                    .title("Task Title " + i)
                    .description("Task Description " + i)
                    .isCompleted(false)
                    .createdAt(LocalDateTime.now().minusMinutes(i))
                    .build());
        }

        // when
        List<Task> result = underTest.findTop5ByIsCompletedFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Task::getTitle).containsExactly("Task Title 3", "Task Title 2", "Task Title 1");
    }

    @Test
    void shouldExcludeCompletedTasks() {
        // given
        underTest.save(Task.builder()
                .title("Task 1")
                .description("Description 1")
                .isCompleted(false)
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .build());
        underTest.save(Task.builder()
                .title("Task 2")
                .description("Description 2")
                .isCompleted(true)
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .build());
        underTest.save(Task.builder()
                .title("Task 3")
                .description("Description 3")
                .isCompleted(false)
                .createdAt(LocalDateTime.now().minusMinutes(3))
                .build());
        underTest.save(Task.builder()
                .title("Task 4")
                .description("Description 4")
                .isCompleted(true)
                .createdAt(LocalDateTime.now().minusMinutes(4))
                .build());
        underTest.save(Task.builder()
                .title("Task 5")
                .description("Description 5")
                .isCompleted(false)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build());

        // when
        List<Task> result = underTest.findTop5ByIsCompletedFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Task::getTitle).containsExactly("Task 5", "Task 3", "Task 1");
    }
    @Test
    void shouldHandleEmptyDatabase() {
        // when
        List<Task> result = underTest.findTop5ByIsCompletedFalseOrderByCreatedAtDesc();

        // then
        assertThat(result).isEmpty();
    }

}
