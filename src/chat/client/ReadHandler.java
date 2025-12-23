package chat.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;

public class ReadHandler implements Runnable {

    private final DataInputStream input;
    private final Client client;
    public boolean closed = false;

    private final Lock lock = new ReentrantLock();

    public ReadHandler(DataInputStream input, Client client) {
        this.input = input;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String received = input.readUTF();
                System.out.println(received);
            }
        } catch (IOException e) {
            log(e);
        } finally {
            client.close();
        }
    }
    
    public void close() {
        
        lock.lock();
        
        try {
            if (closed) {
                return;
            }

            // 종료 로직 필요시 작성
            closed = true;
            log("readHandler 종료");
        } finally {
            lock.unlock();
        }
    }
}
