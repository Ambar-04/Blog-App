package com.springboot.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})}
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String body;

    @ManyToOne(fetch = FetchType.LAZY) // only fetch the related entity from database
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Post post;
}
