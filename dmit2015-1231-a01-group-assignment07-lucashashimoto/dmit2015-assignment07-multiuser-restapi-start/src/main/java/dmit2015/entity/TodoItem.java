package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TodoItemMultiUser")
@Getter @Setter
@NoArgsConstructor
public class TodoItem implements Serializable {

    @Column(nullable = false, length = 32)
    private String username;

    @Id                 // This is the primary key field
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // This primary key field is generated by the database
    private Long id;

    @NotBlank(message = "Task cannot be blank.")
    @Column(nullable = false, length = 64)
    private String task;

    private boolean done;

    @Version
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    private void beforePersist() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updateTime = LocalDateTime.now();
    }

    public TodoItem(Long id, String task, boolean done) {
        this.id = id;
        this.task = task;
        this.done = done;
    }

}