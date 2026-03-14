package de.healthhub.util;

import de.healthhub.infrastructure.PasswordHasher;

public class HashAdminPassword {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: HashAdminPassword <password>");
            System.exit(1);
        }

        String password = args[0];

        PasswordHasher hasher = new PasswordHasher();
        String hash = hasher.hash(password);

        System.out.println(hash);
    }
}