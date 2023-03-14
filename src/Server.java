import org.example.log.Format;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;
public class Server {
    /**
     * Создать клиент-серверный калькулятор для работы с вещественными числами.
     * Вычисление должно быть на сервере. Принимаем выражение для вычисления от пользователя на клиенте.
     * *Добавить возможность логирования.
     * **Можно усложнить, если клиент будет отправлять выражение с несколькими действиями сразу,
     *  например: 3*10-(20+14)/2 (используя стек для парсинга).
     */
    public static void main(String[] args) {
        Logger servLogger = Logger.getLogger(Server.class.getName());
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
        servLogger.setLevel(Level.FINE);
        servLogger.addHandler(new ConsoleHandler());

        try {
            FileHandler fh = new FileHandler("server.log");
            fh.setFormatter(new Format());
            servLogger.addHandler(fh);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started, waiting for connection....");
            servLogger.log(Level.INFO, "Server started, waiting for connection....");
            Socket socket = serverSocket.accept();
            System.out.println("The client has connected!");
            servLogger.log(Level.INFO, "The client has connected!");
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                String clientRequest = dataInputStream.readUTF();
                if (clientRequest.equals("end")) break;

                System.out.println("We got the expression: " + clientRequest);
                servLogger.log(Level.INFO, clientRequest);

                Object result = Calc.eval(clientRequest);


                dataOutputStream.writeUTF("Expression result= " + result);
                servLogger.log(Level.INFO, String.valueOf(result));
            }
        } catch (IOException e) {
            e.printStackTrace();
            servLogger.log(Level.WARNING, e.getMessage());
        }
    }
}
