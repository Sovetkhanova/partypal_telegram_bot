package com.example.partypal.models.entities.telegram;

import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "document", schema = "partypal_tg")
public class Document extends BaseEntity {
    @Column(name = "tg_id")
    private String tgId;

    @Column(name = "tg_unique_id")
    private String tgUniqueId;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @OneToOne(mappedBy = "document")
    private Event event;

}
