package org.example.adoptionpostservice.config;


import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * If Repository is empty initialize with 3 entries
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdoptionPostRepository adoptionPostRepository;

    DataInitializer(AdoptionPostRepository adoptionPostRepository) {
        this.adoptionPostRepository = adoptionPostRepository;
    }

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
                            .imageBase64(encodeImage("zuko.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Capitan Polpetta")
                            .description("Cane tranquillo, adatto anche in appartamento.")
                            .species("Cane")
                            .breed("Bassotto")
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



private String encodeImage(String fileName) {
    try {
        ClassPathResource imgFile = new ClassPathResource("images/" + fileName);
        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
        return Base64.getEncoder().encodeToString(bytes);
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

}