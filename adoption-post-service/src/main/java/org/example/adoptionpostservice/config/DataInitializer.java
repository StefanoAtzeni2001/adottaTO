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
                            .imageBase64(encodeImage("Zuko.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Fido")
                            .description("Cane Labrador Retriever molto dolce e giocoso.")
                            .species("Cane")
                            .breed("Labrador Retriever")
                            .gender("M")
                            .age(36) // mesi
                            .color("Beige")
                            .location("Milano")
                            .publicationDate(LocalDateTime.now().minusDays(2))
                            .ownerId(1L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Fido.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Micia")
                            .description("Gatto Persiano tranquillo e affettuoso.")
                            .species("Gatto")
                            .breed("Persiano")
                            .gender("F")
                            .age(24)
                            .color("Bianco")
                            .location("Roma")
                            .publicationDate(LocalDateTime.now().minusDays(5))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Micia.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Piuma")
                            .description("Pappagallo chiacchierone e intelligente.")
                            .species("Uccello")
                            .breed("Pappagallo")
                            .gender("M")
                            .age(12)
                            .color("Verde")
                            .location("Firenze")
                            .publicationDate(LocalDateTime.now().minusDays(3))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Piuma.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Shelly")
                            .description("Testuggine di Hermann sana e longeva.")
                            .species("Tartaruga")
                            .breed("Testuggine di Hermann")
                            .gender("F")
                            .age(60)
                            .color("Marrone")
                            .location("Napoli")
                            .publicationDate(LocalDateTime.now().minusDays(10))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Shelly.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Nemo")
                            .description("Pesce Rosso vivace e colorato.")
                            .species("Pesce")
                            .breed("Pesce Rosso")
                            .gender("M")
                            .age(6)
                            .color("Rosso")
                            .location("Torino")
                            .publicationDate(LocalDateTime.now().minusDays(1))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Nemo.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Lucky")
                            .description("Cane Meticcio affettuoso e fedele.")
                            .species("Cane")
                            .breed("Meticcio")
                            .gender("F")
                            .age(2)
                            .color("Tigrato")
                            .location("Bologna")
                            .publicationDate(LocalDateTime.now().minusDays(7))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Lucky.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Luna")
                            .description("Gatto Maine Coon con pelo folto e morbido.")
                            .species("Gatto")
                            .breed("Maine Coon")
                            .gender("F")
                            .age(30)
                            .color("Grigio")
                            .location("Genova")
                            .publicationDate(LocalDateTime.now().minusDays(15))
                            .ownerId(2L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Luna.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Coco")
                            .description("Cocorita vivace e socievole.")
                            .species("Uccello")
                            .breed("Cocorita")
                            .gender("M")
                            .age(18)
                            .color("Verde")
                            .location("Verona")
                            .publicationDate(LocalDateTime.now().minusDays(9))
                            .ownerId(3L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Coco.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Torty")
                            .description("Tartaruga Florida resistente e tranquilla.")
                            .species("Tartaruga")
                            .breed("Tartaruga Florida")
                            .gender("F")
                            .age(72)
                            .color("Verde")
                            .location("Palermo")
                            .publicationDate(LocalDateTime.now().minusDays(12))
                            .ownerId(3L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Torty.jpg"))
                            .build()
            );

            adoptionPostRepository.save(
                    AdoptionPost.builder()
                            .name("Bubbles")
                            .description("Betta Splendens coloratissimo e vivace.")
                            .species("Pesce")
                            .breed("Betta Splendens (Pesce Combattente)")
                            .gender("M")
                            .age(8)
                            .color("Arancione")
                            .location("Padova")
                            .publicationDate(LocalDateTime.now().minusDays(4))
                            .ownerId(3L)
                            .active(true)
                            .adopterId(null)
                            .imageBase64(encodeImage("Bubbles.jpg"))
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