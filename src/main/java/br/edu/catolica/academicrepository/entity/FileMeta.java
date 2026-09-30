package br.edu.catolica.academicrepository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "files")
@Document(indexName = "fileindex")
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Integer)
    private Long size;

    @Field(type = FieldType.Text)
    private String mimeType;

    @Field(type = FieldType.Text)
    private String storagePath;

    @Field(type = FieldType.Date_Nanos)
    private Instant createdAt = Instant.now();

    @Field(type = FieldType.Text)
    private String author;

    @Field(type = FieldType.Text)
    private String course;

    @Field(type = FieldType.Text)
    private String advisor;

    @ManyToOne
    @Field(type = FieldType.Integer, name = "folderid")
    @JoinColumn(name = "folder_id")
    private Folder folder;
}
