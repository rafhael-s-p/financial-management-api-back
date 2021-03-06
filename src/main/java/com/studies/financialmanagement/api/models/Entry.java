package com.studies.financialmanagement.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.studies.financialmanagement.api.repositories.listener.AttachmentEntryListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AttachmentEntryListener.class)
@Entity
@Table
public class Entry {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    private String description;

    @NotNull
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @NotNull
    @Column
    private BigDecimal price;

    @Column
    private String observation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type")
    private EntryType entryType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnoreProperties("contacts")
    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    private String attachment;

    @Transient
    private String attachmentUrl;

    @JsonIgnore
    public boolean isEarning() {
        return EntryType.EARNING.equals(entryType);
    }

}
