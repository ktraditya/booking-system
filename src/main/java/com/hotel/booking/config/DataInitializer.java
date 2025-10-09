package com.hotel.booking.config;

import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Initialize admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("System Administrator");
            admin.setRole(User.UserRole.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            log.info("Created admin user - username: admin, password: admin123");
        }

        // Initialize sample rooms if not exists
        if (roomRepository.count() == 0) {
            Room room1 = new Room();
            room1.setRoomNumber("101");
            room1.setType(Room.RoomType.SINGLE);
            room1.setPricePerNight(100.0);
            room1.setCapacity(1);
            room1.setSize(20.0);
            room1.setBedType("Single");
            room1.setDescription("Cozy single room");
            room1.setAmenities(Arrays.asList("WiFi", "TV", "AC"));
            room1.setFloor(1);
            room1.setView("City");
            room1.setSmokingAllowed(false);
            room1.setPetFriendly(false);
            room1.setIsAvailable(true);

            Room room2 = new Room();
            room2.setRoomNumber("201");
            room2.setType(Room.RoomType.DOUBLE);
            room2.setPricePerNight(150.0);
            room2.setCapacity(2);
            room2.setSize(30.0);
            room2.setBedType("Queen");
            room2.setDescription("Comfortable double room");
            room2.setAmenities(Arrays.asList("WiFi", "TV", "AC", "Mini-bar"));
            room2.setFloor(2);
            room2.setView("Garden");
            room2.setSmokingAllowed(false);
            room2.setPetFriendly(false);
            room2.setIsAvailable(true);

            Room room3 = new Room();
            room3.setRoomNumber("301");
            room3.setType(Room.RoomType.SUITE);
            room3.setPricePerNight(300.0);
            room3.setCapacity(4);
            room3.setSize(60.0);
            room3.setBedType("King");
            room3.setDescription("Luxurious suite with premium amenities");
            room3.setAmenities(Arrays.asList("WiFi", "TV", "AC", "Mini-bar", "Jacuzzi", "Balcony"));
            room3.setFloor(3);
            room3.setView("Ocean");
            room3.setSmokingAllowed(false);
            room3.setPetFriendly(true);
            room3.setIsAvailable(true);

            roomRepository.saveAll(Arrays.asList(room1, room2, room3));
            log.info("Created sample rooms");
        }

        log.info("===========================================");
        log.info("Data initialization completed!");
        log.info("Admin credentials - username: admin, password: admin123");
        log.info("===========================================");
    }
}
