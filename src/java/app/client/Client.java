package app.client;

import app.dto.DocumentDto;
import app.dto.UserDto;
import app.error.ErrorResponse;
import app.enums.MsgType;
import app.connection.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Client implements Runnable {
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    private static final String EMAIL_KEY = "-email";
    private static final String PASSWORD_KEY = "-password";
    private static final String ID_KEY = "-id";

    private final String host;
    private final int port;
    private final Scanner scanner;
    private final PrintWriter writer;
    private UserDto me = null;

    public Client() {
        Scanner scanner = new Scanner(System.in);
        PrintWriter writer = new PrintWriter(System.out, true);
        writer.println("Enter host");
        this.host = scanner.nextLine();
        writer.println("Enter port");
        this.port = scanner.nextInt();
        this.scanner = scanner;
        this.writer = writer;
    }

    @Override
    public void run() {
        Thread printerThread = null;
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            printerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Message request = (Message) in.readObject();
                        printResponse(request);
                    } catch (IOException e) {
                        break;
                    } catch (Exception e) {
                        handleException(e);
                    }
                }
            });
            printerThread.start();

            Message[] messages = {
                    buildRequest("register -email vechorco@gmail.com -password brawlstars"),
                    buildRequest("register -email misha@gmail.com -password boroda"),
                    buildRequest("register -email zahar@gmail.com -password harosh"),
            };

            printResponse(buildRequest(MsgType.HELP.name()));
            if (scanner.hasNextLine())
                scanner.nextLine();
            writer.println("Enter commands");
            while (true) {
                try {
                    String line = scanner.nextLine();
                    Message msg = buildRequest(line);
                    if (msg.getType() == MsgType.EXIT)
                        break;
                    if (msg.getType() == MsgType.HELP) {
                        printResponse(msg);
                        continue;
                    }
                    out.reset();
                    out.writeObject(msg);
                    out.flush();
                } catch (SocketException e) {
                    break;
                } catch (Exception e) {
                    handleException(e);
                }
            }
        } catch (IOException e) {
            handleException(e);
        } finally {
            if (printerThread != null)
                printerThread.interrupt();
        }
    }

    private Message buildRequest(String line) {
        String[] args = line.split("\\s+");
        String command = args[0];
        MsgType type = MsgType.valueOf(command.toUpperCase(Locale.ROOT));
        Object[] requestParams;
        switch (type) {
            case HELP: {
                StringJoiner joiner = new StringJoiner("\n");
                for (MsgType msgType : MsgType.values()) {
                    String name = msgType.name();
                    String toLowerCase = name.toLowerCase();
                    joiner.add(toLowerCase);
                }
                String help = joiner.toString();
                requestParams = params(help);
            }
            break;
            case REGISTER:
            case AUTH: {
                String email = getValue(args, 1, args.length, EMAIL_KEY);
                String password = getValue(args, 1, args.length, PASSWORD_KEY);
                UserDto dto = new UserDto(null, email, password, null, null);
                requestParams = params(dto);
            }
            break;
            case USER_FIND_BY_ID:
            case USER_DELETE_BY_ID: {
                Integer userId = getId(args, true, Integer::parseInt);
                requestParams = params(me, userId);
            }
            break;
            case USER_FIND_ALL:
            case USER_DELETE_ALL:
            case DOCUMENT_FIND_ALL:
            case DOCUMENT_DELETE_ALL:
                requestParams = params(me);
                break;
            case DOCUMENT_SAVE: {
                String text = readTestFromPath(args);
                Long documentId = getId(args, false, Long::parseLong);
                DocumentDto target = new DocumentDto(documentId, text);
                requestParams = params(me, target);
            }
            break;
            case DOCUMENT_FIND_BY_ID:
            case DOCUMENT_DELETE_BY_ID: {
                Long documentId = getId(args, true, Long::parseLong);
                requestParams = params(me, documentId);
            }
            break;
            default:
                requestParams = null;
        }
        return new Message(type, requestParams);
    }

    private void printResponse(Message request) {
        Object[] args = request.getArgs();

        writer.println(request.getType().name().toLowerCase(Locale.ROOT));
        switch (request.getType()) {
            case HELP:
                writer.println(args[0]);
                break;
            case REGISTER:
            case AUTH:
                me = (UserDto) args[0];
                break;
            case USER_FIND_BY_ID:
                UserDto userDto = (UserDto) args[0];
                writer.println(userDto);
                break;
            case USER_FIND_ALL:
            case DOCUMENT_FIND_ALL: {
                StringJoiner joiner = new StringJoiner("\n");
                for (Object arg : args) {
                    String s = String.valueOf(arg);
                    joiner.add(s);
                }
                String argsStr = joiner.toString();
                writer.println(argsStr);
                break;
            }
            case DOCUMENT_FIND_BY_ID:
            case DOCUMENT_SAVE:
                DocumentDto documentDto = (DocumentDto) args[0];
                writer.println(documentDto);
                break;
            case ERROR:
                ErrorResponse response = (ErrorResponse) args[0];
                writer.println(response);
                break;
        }
    }

    protected void handleException(Exception e) {
        writer.printf("%s-%s\n", e.getClass().getSimpleName(), e.getMessage());
    }

    private static String readTestFromPath(String[] args) {
        return getValue(args, 1, args.length, "-path", true, s -> {
            try {
                return Files.readString(Paths.get(s));
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    private static <T extends Number> T getId(String[] args, boolean required, Function<String, T> mapper) {
        return getValue(args, 1, args.length, ID_KEY, required, s -> {
            try {
                return mapper.apply(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("id is not a number");
            }
        });
    }

    private static Object[] params(Object... params) {
        return params;
    }

    public static String getValue(String[] args, int from, int to, String key) {
        int keyPos = IntStream.range(from, to)
                .filter(value -> key.equals(args[value]))
                .findAny()
                .orElse(-1);
        if (keyPos < 0)
            throw new NoSuchElementException(String.format("key '%s' not found", key));
        int valuePos = keyPos + 1;
        if (valuePos > to) {
            throw new NoSuchElementException(String.format("value not found after key '%s'", key));
        }
        return args[valuePos];
    }

    public static <T> T getValue(String[] args, int from, int to, String key,
                                 boolean required, Function<String, T> mapper) {
        try {
            String value = getValue(args, from, to, key);
            return mapper.apply(value);
        } catch (NoSuchElementException e) {
            if (required)
                throw e;
            return null;
        }
    }
}

