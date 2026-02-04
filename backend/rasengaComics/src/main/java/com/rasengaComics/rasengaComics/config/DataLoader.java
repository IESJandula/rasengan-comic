package com.rasengaComics.rasengaComics.config;

import com.rasengaComics.rasengaComics.entities.Product;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            // Solo cargar productos si la base de datos está vacía
            if (productRepository.count() == 0) {
                List<Product> products = Arrays.asList(
                    new Product("Attack on Titan Vol. 34", "Manga", "Shonen", 11.99, null, null, "attack-on-titan-vol34.jpg", true, 5.0, 789, false, true),
                    new Product("Batman: The Dark Knight", "Comics", "DC", 24.99, 29.99, 17, "batman-the-dark-knight.jpg", true, 5.0, 456, false, false),
                    new Product("Batman: Year One", "Comics", "DC", 14.99, null, null, "batman-year-one.jpg", true, 4.0, 134, false, false),
                    new Product("Berserk Deluxe Vol. 8", "Manga", "Seinen", 39.99, 46.99, 15, "berserk-deluxe-vol8.jpg", true, 5.0, 567, false, false),
                    new Product("Catan: Base Game", "Juegos de mesa", "Estrategia", 49.99, null, null, "catan-base-game.jpg", true, 5.0, 312, false, false),
                    new Product("Chainsaw Man Vol. 14", "Manga", "Shonen", 11.99, null, null, "Chainsaw man vol14.jpg", false, 5.0, 289, true, false),
                    new Product("Custom Playmat", "Accesorios", "Playmat", 29.99, 37.49, 20, "custom-paymat.jpg", true, 4.0, 198, false, false),
                    new Product("Dados Metálicos Set", "Accesorios", "Dados", 34.99, null, null, "dados-metalicos-set.jpg", true, 5.0, 156, false, false),
                    new Product("Dragon Shield Playmat", "Accesorios", "Playmat", 24.99, null, null, "dragon-shield-playmat.jpg", true, 5.0, 289, false, false),
                    new Product("Dragon Shield Sleeves", "Accesorios", "Fundas", 12.99, null, null, "dragon-shield-sleeves.jpg", true, 5.0, 421, false, true),
                    new Product("Funko Goku", "Figuras", "Funko Pop", 14.99, null, null, "funko-goku.jpg", true, 4.0, 178, false, false),
                    new Product("Gojo Satoru Nendoroid", "Figuras", "Nendoroid", 54.99, null, null, "gojo-satoru-nendoroid.jpg", false, 5.0, 445, true, false),
                    new Product("Goku Ultra Instinct Statue", "Figuras", "Estatuas", 249.99, null, null, "goku-ultra-instinct-statue.jpg", false, 5.0, 234, true, false),
                    new Product("Jujutsu Kaisen Vol. 20", "Manga", "Shonen", 11.99, null, null, "jujutsu-kaisen-20.jpg", true, 5.0, 892, false, true),
                    new Product("Jujutsu Kaisen Vol. 22", "Manga", "Shonen", 11.99, null, null, "jujutsu-kaisen-vol-22.jpg", true, 5.0, 678, false, false),
                    new Product("Luffy Gear 5", "Figuras", "Scale Figures", 129.99, 144.99, 10, "luffy-gear-5.jpg", true, 5.0, 567, false, false),
                    new Product("Luffy Gear 5 Figure 2", "Figuras", "Scale Figures", 139.99, null, null, "luffy-gear-5-figure2.jpg", false, 5.0, 345, true, false),
                    new Product("Magic Commander", "TCG", "Magic", 159.99, 199.99, 20, "magicComander.jpg", true, 5.0, 890, false, false),
                    new Product("MTG Booster Pack", "TCG", "Magic", 4.99, null, null, "mtg-booster-pack.jpg", true, 4.0, 234, false, false),
                    new Product("MTG Commander Legends", "TCG", "Magic", 159.99, 188.22, 15, "mtg-commander-legends.jpg", true, 5.0, 456, false, false),
                    new Product("Naruto Figure", "Figuras", "Nendoroid", 34.99, null, null, "naruto-figure.jpg", true, 5.0, 678, false, false),
                    new Product("Nendoroid Gojo", "Figuras", "Nendoroid", 54.99, null, null, "nendoroid-gojo.jpg", true, 5.0, 789, false, true),
                    new Product("One Piece Vol. 100", "Manga", "Shonen", 12.99, 14.99, 10, "one-piece-vol100.jpg", true, 5.0, 1567, false, true),
                    new Product("Pandemic", "Juegos de mesa", "Cooperativos", 39.99, null, null, "pandemic.png", true, 5.0, 890, false, false),
                    new Product("Pandora Vol. 24", "Manga", "Shojo", 11.99, null, null, "pandora-vol24.jpg", true, 4.0, 345, false, false),
                    new Product("Pokémon Paradox Rift", "TCG", "Pokemon", 4.50, null, null, "pokemon paradox rift.jpg", true, 5.0, 567, false, true),
                    new Product("Pokémon Card Sleeve", "Accesorios", "Fundas", 8.99, null, null, "pokemon-card-sleeve.jpg", true, 4.0, 456, false, false),
                    new Product("Scarlet Violet Booster", "TCG", "Pokemon", 4.50, null, null, "scarlet-violet-booster.jpg", true, 5.0, 678, false, false),
                    new Product("Spider-Man Comic", "Comics", "Marvel", 5.99, 6.99, 15, "Spider-Man-1.jpg", true, 4.0, 234, false, false),
                    new Product("Spider-Man Comic 2", "Comics", "Marvel", 5.99, null, null, "Spider-Man-comic.jpg", true, 4.0, 345, false, false),
                    new Product("Superman: Action Comics", "Comics", "DC", 18.99, 21.09, 10, "super-man-action-comics.jpg", true, 4.0, 456, false, false),
                    new Product("Ticket to Ride", "Juegos de mesa", "Familiar", 44.99, null, null, "ticket to ride.jpg", true, 5.0, 789, false, false),
                    new Product("Ticket to Ride Europa", "Juegos de mesa", "Familiar", 44.99, null, null, "ticket-to-ride-europe.jpg", true, 5.0, 890, false, false),
                    new Product("Tokyo Ghoul Manga Box Set", "Manga", "Seinen", 179.99, 257.13, 30, "tokyo-ghoul-manga-box-set.jpg", true, 5.0, 1234, false, false),
                    new Product("Ultimate Guard Deck Box", "Accesorios", "Deck Box", 9.99, null, null, "ultimate-guard-deck-box.jpg", true, 4.0, 567, false, false),
                    new Product("Walking Dead", "Comics", "Image", 59.99, null, null, "walking-dead.jpg", true, 5.0, 678, false, false),
                    new Product("Wingspan", "Juegos de mesa", "Estrategia", 54.99, 73.32, 25, "wingspan.jpg", true, 5.0, 890, false, false),
                    new Product("Wingspan Expansions", "Juegos de mesa", "Estrategia", 29.99, null, null, "wingspan-expansions.jpg", true, 5.0, 456, false, false),
                    new Product("Wolverine Bust", "Figuras", "Bustos", 89.99, null, null, "wolverine-bust.jpg", true, 4.0, 234, false, false),
                    new Product("Yu-Gi-Oh! Booster Box", "TCG", "Yu-Gi-Oh", 89.99, 119.99, 25, "yu-gi-oh booster.jpg", true, 5.0, 789, false, false)
                );

                productRepository.saveAll(products);
                System.out.println("✅ Base de datos poblada con " + products.size() + " productos");
            } else {
                System.out.println("ℹ️  La base de datos ya contiene productos. No se cargaron datos iniciales.");
            }
        };
    }
}
