package monty;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
	public static void main(String... args) {
		// ApplicationContext ctx =
		SpringApplication.run(App.class, args);

		/*
		 * String[] beanNames = ctx.getBeanDefinitionNames();
		 * Arrays.sort(beanNames); for (String beanName : beanNames) {
		 * System.out.println(beanName); }
		 */

	}
}
