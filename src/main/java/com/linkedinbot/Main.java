package com.linkedinbot;

import com.linkedinbot.modules.JobApplier;
import com.linkedinbot.validation.ConfigValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.Scanner;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("==========================================");
        System.out.println("  LinkedIn Auto Applier - Java Edition");
        System.out.println("==========================================");
        System.out.println("[1] Start Bot");
        System.out.println("[2] Open Web Dashboard");
        System.out.println("[3] Validate Config Only");
        System.out.print("Enter choice: ");

        Scanner sc = new Scanner(System.in);
        String choice = sc.nextLine().trim();

        if (choice.equals("1")) {
            System.out.println("Validating config...");
            ConfigValidator.validateAll();
            System.out.println("Starting bot...");
            JobApplier applier = new JobApplier();
            applier.run();

        } else if (choice.equals("2")) {
            System.out.println("Starting dashboard at http://localhost:5000");
            ConfigurableApplicationContext ctx =
                SpringApplication.run(Main.class, args);
            System.out.println("Dashboard running!");
            System.out.println("Open http://localhost:5000 in your browser.");
            System.out.println("Press ENTER to stop the server...");
            sc.nextLine();
            ctx.close();
            System.out.println("Server stopped.");

        } else if (choice.equals("3")) {
            System.out.println("Validating config...");
            ConfigValidator.validateAll();
            System.out.println("All config values are valid!");

        } else {
            System.out.println("Invalid choice. Please enter 1, 2 or 3.");
        }

        sc.close();
    }
}