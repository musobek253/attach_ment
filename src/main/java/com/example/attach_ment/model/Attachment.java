package com.example.attach_ment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String orginalName;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String ContentType;

    private String name;


    public Attachment(String orginalName, long size, String contentType) {
        this.orginalName = orginalName;
        this.size = size;
        ContentType = contentType;
    }
}
