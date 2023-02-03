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
        handlers.putIfAbsent(method, new ConcurrentHashMap<>());
        ConcurrentHashMap<String, Handler> methodMap = handlers.get(method);
        methodMap.put(path, handler);
    }


    public void handle(Socket clientSocket) {
        try( final var out = new BufferedOutputStream(clientSocket.getOutputStream());
             final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            while (true) {
                final var requestLine = in.readLine();
                if (requestLine == null) {
                    break;
                    //continue;
                }

                Request request = new Request(requestLine);
                var methodMap = handlers.get(request.getMethod());
                if (methodMap == null) {
                    sendError(out);
                    return;
                }

                Handler handler = methodMap.get(request.getResourcePath());
                if (handler == null) {
                    sendError(out);
                    return;
                }
                handler.handle(request, out);
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
/*
                System.out.println("New client connected"
                        + client.getInetAddress()
                        .getHostAddress());
*/
                    executorService.execute(() -> handle(client));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    /*
    public void run() throws IOException {

        try {
            server = new ServerSocket(serverPort);
            server.setReuseAddress(true);

            ExecutorService executorService = Executors.newFixedThreadPool(64);

            while (true) {

                Socket client = server.accept();
                executorService.execute(new ClientHandler(client));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;


        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
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

        protected static void sendClassic(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
        }

        protected static void sendFile(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();

        }

        public void handle(Socket client) {
            try( final var out = new BufferedOutputStream(clientSocket.getOutputStream());
                 final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                while (true) {
                    final var requestLine = in.readLine();
                    if (requestLine == null) {
                        break;
                        //continue;
                    }

                    Request request = new Request(requestLine);
                    var methodMap = handlers.get(request.getMethod());
                    if(methodMap == null) {
                        sendError(out);
                        return;
                    }

                    var handler =  methodMap.get(request.getResourcePath());
                    if(handler == null) {
                        sendError(out);
                        return;
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
        */



  /*      public void run() {
            BufferedOutputStream out = null;
            BufferedReader in = null;
            try {

                out = new BufferedOutputStream(clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));




                while (true) {
                    final var requestLine = in.readLine();
                    if (requestLine == null) {
                        break;
                        //continue;
                    }

                    Request request = new Request(requestLine);
                    var methodMap = handlers.get(request.getMethod());
                    if(methodMap == null) {
                        sendError(out);
                        return;
                    }

                    var handler =  methodMap.get(request.getResourcePath());
                    if(handler == null) {
                        sendError(out);
                        return;
                    }
                    try {
                       // handler.handle(request, out);
                    } catch (IOException e) {
                        e.printStackTrace();
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

   */


}
