package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Raul Palade
 * @project Email Server
 * @date 30/03/2021
 */
public class ThreadPoolServer extends Thread implements Runnable {
    private final DataModel dataModel;
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private Socket socket;
    private ServerSocket serverSocket;

    public ThreadPoolServer(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public void run() {
        System.out.println("RUN SERVER");
        try {
            serverSocket = new ServerSocket(7777);
            while (true) {
                socket = serverSocket.accept();
                System.out.println("ACCEPT");
                pool.execute(new ServerTask(socket, dataModel));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (pool != null) {
            pool.shutdown();
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}