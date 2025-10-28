package epam.gym;


import epam.gym.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymApplicationJava {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        System.out.println(ctx.containsBean("traineeService"));
    }
}
