package ru.netology;


import java.io.IOException;


public class Main {
    public static void main(String[] args) {

        final var server = new Server(9999);
        // код инициализации сервера (из вашего предыдущего ДЗ)

        // добавление handler'ов (обработчиков)
        server.addHandler("GET", "/messages", (request, out) -> {

            String greeting = "Hello from GET /messages";
            try {
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + "text/plain" + "\r\n" +
                                "Content-Length: " + greeting.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(greeting.getBytes());
                out.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        server.addHandler("POST", "/messages", (request, out) -> {

            String greeting = "Hello from POST /messages";

            try {
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + "text/plain" + "\r\n" +
                                "Content-Length: " + greeting.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(greeting.getBytes());
                out.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        server.start();


    }
}


