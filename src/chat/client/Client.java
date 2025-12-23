package chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static network.tcp.SocketCloseUtil.closeAll;
import static util.MyLogger.log;

public class Client {
    
    private final String host;
    private final int port;
    
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    private ReadHandler readHandler;
    private WriteHandler writeHandler;
    private boolean closed = false;
    
    private Lock lock = new ReentrantLock();

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        log("클라이언트 시작");
        socket = new Socket(host, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        
        readHandler = new ReadHandler(input, this);
        writeHandler = new WriteHandler(output, this);

        Thread readThread = new Thread(this.readHandler, "readHandler");
        Thread writeThread = new Thread(this.writeHandler, "writeHandler");
        readThread.start();
        writeThread.start();
    }
    
    public void close() {
        lock.lock();
        
        try {
            
            if (closed) {
                return;
            }
            
            writeHandler.close();
            readHandler.close();
            closeAll(socket, input, output);
            closed = true;
            log("연결 종료 : " + socket);
            
        } finally {
            lock.unlock();
        }
    }
}
