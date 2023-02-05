package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static java.lang.System.in;
import static java.lang.System.out;


public class Server {

    protected static int serverPort;
    //ServerSocket server = null;

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>>();

    public Server(int serverPort) {
        this.serverPort = serverPort;

    }

    protected static void sendError(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public void addHandler(String method, String path, Handler handler) {
        ConcurrentHashMap<String, Handler> methodMap = new ConcurrentHashMap<>();
        methodMap.put(path, handler);
        handlers.putIfAbsent(method, methodMap);
    }


    public void handle(Socket clientSocket) {
        try (final var out = new BufferedOutputStream(clientSocket.getOutputStream());
             final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            while (true) {
                final var requestLine = in.readLine();

                if (requestLine == null) {
                    continue;
                }

                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                    continue;
                }
                Request request = new Request(requestLine);
                request.getQueryParams()
                        .stream()
                        .forEach(System.out::println);


                request.getQueryParam("value").forEach(System.out::println);

                request.getQueryParam("last").forEach(System.out::println);

                System.out.println(Thread.currentThread().getName() + "received a request " + requestLine);


                var methodMap = handlers.get(request.getMethod());
                if (methodMap != null) {
                    Handler handler = methodMap.get(request.getResourcePath());
                    if (handler != null) {
                        handler.handle(request, out);
                    } else {
                        sendError(out);
                    }
                } else {
                    sendError(out);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void start() {
        try (final var server = new ServerSocket(serverPort)) {
            server.setReuseAddress(true);

            ExecutorService executorService = Executors.newFixedThreadPool(64);

            while (true) {

                Socket client = server.accept();

                System.out.println("New client connected "
                        + client.getInetAddress()
                        .getHostAddress());

                executorService.execute(() -> handle(client));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
