package org.example.adoptionpostservice.config;


import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
/**
 * If Repository is empty initialize with 3 entries
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdoptionPostRepository adoptionPostRepository;

    @Override
    public void run(String... args) {
        if (adoptionPostRepository.count() == 0) {
            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Zuko")
                            .description("Gatto indemoniato, cerca famiglia amorevole.")
                            .species("Gatto")
                            .breed("Soriano")
                            .gender("M")
                            .age(24)
                            .color("Rosso")
                            .location("Torino")
                            .publicationDate(LocalDateTime.now().minusDays(1))
                            .ownerId(1L)
                            .active(true)
                            .adopterId(null)
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Capitan Polpetta")
                            .description("Cane tranquillo, adatto anche in appartamento.")
                            .species("Cane")
                            .breed("Labrador")
                            .gender("M")
                            .age(3)
                            .color("Marrone")
                            .location("Milano")
                            .publicationDate(LocalDateTime.now().minusDays(4))
                            .ownerId(1L)
                            .active(true)
                            .adopterId(null)
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Michelangelo")
                            .description("Grande amante della pizza, da tenere d'occhio...")
                            .species("Tartaruga")
                            .breed("Hermanni")
                            .gender("F")
                            .age(12)
                            .color("Verde")
                            .location("Milano")
                            .publicationDate(LocalDateTime.now().minusWeeks(1))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Megatron")
                            .description("Animale già adottato")
                            .species("Capibara")
                            .breed("Meticcio")
                            .gender("F")
                            .age(72)
                            .color("Marrone")
                            .location("Milano")
                            .publicationDate(LocalDateTime.now().minusWeeks(8))
                            .ownerId(2L)
                            .active(false)
                            .adopterId(4L)
                            .build()
            );

            System.out.println(">>> DB initialized");
        }
    }
}