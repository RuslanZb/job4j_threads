package ru.job4j.thread;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;

public class Wget implements Runnable {
    private final String url;
    private final int speed;

    public Wget(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    @Override
    public void run() {
        String[] fileName = url.split("/");
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("data/" + fileName[fileName.length - 1])) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int downloadData = 0;
            long startTime = currentTimeMillis();
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                downloadData += bytesRead;
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                if (downloadData >= speed) {
                    long endTime = currentTimeMillis();
                    int deltaTime = (int) (endTime - startTime);
                    if (deltaTime < 1000) {
                        Thread.sleep(1000 - deltaTime);
                    }
                    downloadData = 0;
                    startTime = currentTimeMillis();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void validate(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Args is empty");
        }
        if (args.length != 2) {
            throw new IllegalArgumentException("The number of arguments must be 2");
        }
        if (!Pattern.matches(".*\\..+\\/.*\\..+", args[0])) {
            throw new IllegalArgumentException("Incorrect url");
        }
        if (!Pattern.matches("\\d+", args[1]) || Integer.parseInt(args[1]) == 0) {
            throw new IllegalArgumentException("Incorrect speed");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        validate(args);
        String url = args[0];
        int speed = Integer.parseInt(args[1]);
        Thread wget = new Thread(new Wget(url, speed));
        wget.start();
        wget.join();
    }
}