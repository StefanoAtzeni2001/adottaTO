package org.example.savedsearchservice.config;

import org.example.savedsearchservice.model.SavedSearch;
import org.example.savedsearchservice.repository.SavedSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SavedSearchRepository savedSearchRepository;

    @Override
    public void run(String... args) {
        if (savedSearchRepository.count() == 0) {
            // Utente 1: cerca solo gatti rossi, max 3 anni
            savedSearchRepository.save(
                    SavedSearch.builder()
                            .userId(1L)
                            .species(List.of("Gatto"))
                            .breed(List.of("Soriano"))
                            .gender(null)
                            .minAge(null)
                            .maxAge(36)
                            .color(List.of("Rosso"))
                            .build()
            );

            // Utente 2: qualsiasi cane femmina sotto 2 anni
            savedSearchRepository.save(
                    SavedSearch.builder()
                            .userId(2L)
                            .species(List.of("Cane"))
                            .breed(null)  // accetta tutte le razze
                            .gender("F")
                            .minAge(null)
                            .maxAge(24)
                            .color(null)
                            .build()
            );

            // Utente 3: qualsiasi animale
            savedSearchRepository.save(
                    SavedSearch.builder()
                            .userId(3L)
                            .species(null)
                            .breed(null)
                            .gender(null)
                            .minAge(null)
                            .maxAge(null)
                            .color(null)
                            .build()
            );
        }
    }
}
