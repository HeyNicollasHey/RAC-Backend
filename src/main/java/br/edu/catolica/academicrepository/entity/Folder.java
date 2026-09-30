package br.edu.catolica.academicrepository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Entity
@Table(name = "folders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "folderindex")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(type= FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "course")
    private String course;

    @Field(type = FieldType.Date_Nanos, name = "createdat")
    private Instant createdAt = Instant.now();

    @ManyToOne
    @Field(type = FieldType.Integer, name = "parent")
    @JoinColumn(name = "parent_id")
    private Folder parent;


}
