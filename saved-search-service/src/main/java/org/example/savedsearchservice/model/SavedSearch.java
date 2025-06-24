package org.example.savedsearchservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "saved_searches")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    @CollectionTable(name = "saved_search_species", joinColumns = @JoinColumn(name = "saved_search_id"))
    @Column(name = "species")
    private List<String> species;

    @ElementCollection
    @CollectionTable(name = "saved_search_breeds", joinColumns = @JoinColumn(name = "saved_search_id"))
    @Column(name = "breed")
    private List<String> breed;

    private String gender;

    private Integer minAge;

    private Integer maxAge;

    @ElementCollection
    @CollectionTable(name = "saved_search_colors", joinColumns = @JoinColumn(name = "saved_search_id"))
    @Column(name = "color")
    private List<String> color;

}
