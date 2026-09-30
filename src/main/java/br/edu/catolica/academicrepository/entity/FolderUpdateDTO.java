package br.edu.catolica.academicrepository.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderUpdateDTO {

    private String name;
    private String course;
}
