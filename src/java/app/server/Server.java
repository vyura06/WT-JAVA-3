package app.server;

import app.connection.Message;
import app.controller.DocumentController;
import app.controller.UserController;
import app.dto.DocumentDto;
import app.dto.UserDto;
import app.enums.MsgType;
import app.error.ErrorResponse;
import app.exception.BaseRuntimeException;
import app.exception.GeneralExceptionHandler;
import app.repository.impl.DocumentRepositoryImpl;
import app.repository.impl.UserRepositoryImpl;
import app.service.AuthService;
import app.service.DocumentService;
import app.service.UserService;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(System.out, true)) {
            File usersPath = new File(absolutePathFromRes("users.xml"));
            File documentsPath = new File(absolutePathFromRes("documents.xml"));
            int port = 5000;
            Server server = new Server(port, usersPath, documentsPath, writer);
            Thread serverThread = new Thread(server);
            serverThread.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                writer.println("Press stop to stop server");
                String line;
                while ((line = reader.readLine()) != null) {
                    if ("stop".equals(line))
                        break;
                }
            } catch (IOException e) {
                server.handleException(e);
            } finally {
                server.stop();
            }
        }
    }

    private static String absolutePathFromRes(String resourcePath) {
        return Objects.requireNonNull(
                Server.class.getClassLoader().getResource(resourcePath)).getPath();
    }

    private volatile boolean running = false;
    private final int port;
    private final UserController userController;
    private final DocumentController documentController;
    private final GeneralExceptionHandler exceptionHandler;
    private final ExecutorService executorService;
    private final PrintWriter writer;
    private ServerSocket serverSocket;

    public Server(int port, File usersPath, File documentsPath, PrintWriter writer) {
        this.port = port;
        this.writer = writer;
        UserService userService = new UserService(new UserRepositoryImpl(usersPath));
        DocumentService documentService = new DocumentService(new DocumentRepositoryImpl(documentsPath));
        AuthService authService = new AuthService(userService);
        this.userController = new UserController(userService, authService);
        this.documentController = new DocumentController(documentService, authService);
        this.exceptionHandler = new GeneralExceptionHandler();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getLocalHost())) {
            this.serverSocket = serverSocket;
            writer.println(serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
            while (running) {
                Socket accept = serverSocket.accept();
                executorService.execute(() -> handleResponse(accept));
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            running = false;
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService.shutdownNow();
    }

    private void handleResponse(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); socket) {
            while (running) {
                try {
                    Message request = (Message) in.readObject();
                    Message response = buildResponse(request);
                    if (response != null)
                        out.writeObject(response);
                } catch (BaseRuntimeException e) {
                    ErrorResponse errorResponse = exceptionHandler.generalHandler(e);
                    out.writeObject(new Message(MsgType.ERROR, new Object[]{errorResponse}));
                } catch (ClassNotFoundException | ClassCastException e) {
                    out.writeObject(new Message(MsgType.ERROR,
                            new Object[]{new ErrorResponse(400, e.getClass().getName(), e.getMessage())}));
                } catch (IOException exception) {
                    break;
                }
                out.flush();
            }
        } catch (SocketException ignored) {
        } catch (Exception e) {
            handleException(e);
        }
    }

    private Message buildResponse(Message request) {
        Object[] args = request.getArgs();
        UserDto user = (UserDto) args[0];
        MsgType type = request.getType();
        switch (type) {
            case REGISTER: {
                UserDto dto = userController.register(user);
                return new Message(type, new Object[]{dto});
            }
            case AUTH: {
                UserDto dto = userController.auth(user);
                return new Message(type, new Object[]{dto});
            }
            case USER_FIND_BY_ID: {
                UserDto dto = userController.findById(user, (Integer) args[1]);
                return new Message(type, new Object[]{dto});
            }
            case USER_FIND_ALL: {
                List<UserDto> all = userController.findAll(user);
                return new Message(type, all.toArray());
            }
            case USER_DELETE_BY_ID: {
                userController.deleteById(user, (Integer) args[0]);
                return new Message(type);
            }
            case USER_DELETE_ALL: {
                userController.deleteAll(user);
                return new Message(type);
            }
            case DOCUMENT_FIND_BY_ID: {
                DocumentDto dto = documentController.findById(user, (Long) args[1]);
                return new Message(type, new Object[]{dto});
            }
            case DOCUMENT_FIND_ALL: {
                List<DocumentDto> all = documentController.findAll(user);
                return new Message(type, all.toArray());
            }
            case DOCUMENT_SAVE: {
                DocumentDto dto = documentController.save(user, (DocumentDto) args[1]);
                return new Message(type, new Object[]{dto});
            }
            case DOCUMENT_DELETE_BY_ID: {
                documentController.deleteById(user, (Long) args[1]);
                return new Message(type);
            }
            case DOCUMENT_DELETE_ALL: {
                documentController.deleteAll(user);
                return new Message(type);
            }
        }
        return null;
    }

    protected void handleException(Exception e) {
        e.printStackTrace(writer);
    }
}
