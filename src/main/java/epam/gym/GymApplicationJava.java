package epam.gym;


import epam.gym.config.ApplicationConfig;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.TrainingType;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class GymApplicationJava {
    public static void main(String[] args) {
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
//        TrainerRepository trainerRepository = ctx.getBean(TrainerRepository.class);
//        TrainingRepository trainingRepository = ctx.getBean(TrainingRepository.class);
//        System.out.println("=== Reading all trainers from database ===");
//        List<Trainer> trainers = trainerRepository.findAll();
//
//        System.out.println("Found " + trainers.size() + " trainers:");
//        for (Trainer trainer : trainers) {
//            System.out.println("  - ID: " + trainer.getId() +
//                             ", Username: " + trainer.getUser().getUsername() +
//                             ", Name: " + trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName() +
//                             ", Specialization: " + (trainer.getSpecialization() != null ? trainer.getSpecialization().getTrainingTypeName() : "N/A") +
//                             ", Active: " + trainer.getUser().getIsActive());
//        }
//
//        ((AnnotationConfigApplicationContext) ctx).close();
    }
}