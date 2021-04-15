package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static common.Utils.PORT;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 30/03/2021
 */
public class ThreadPoolServer extends Thread implements Runnable {
    private final ServerModel serverModel;
    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private ServerSocket serverSocket;

    public ThreadPoolServer(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    threadPool.execute(new ServerTask(socket, serverModel));
                } catch (IOException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() throws IOException {
        threadPool.shutdown();
        serverSocket.close();
    }
}