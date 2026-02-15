package ru.duskhunter.aston.lesson2.utils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ResourceChecker {
    public static void checkLogs() {
        // ПРОВЕРКА 1: Загружается ли Log4j2?
        System.out.println("=== TEST LOGGING ===");

        // ПРОВЕРКА 2: Прямой вывод через System.out
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        System.out.println("Log4j2.xml exists: " +
                ResourceChecker.class.getResource("/log4j2.xml"));

        // ПРОВЕРКА 3: Тестовый лог
        log.info("THIS IS TEST LOG MESSAGE - IF YOU SEE THIS, LOG4J2 WORKS!");
    }
}
