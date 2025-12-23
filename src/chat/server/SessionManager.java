package chat.server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.MyLogger.log;


/**
 * ReentrantLock을 사용한 Thread-Safe 세션 매니저
 * Java 21+ Virtual Thread 환경 대응 (Pinning 이슈 해결)
 */
public class SessionManager {



    // 동시성 관리를 위한 List (ArrayList 자체는 Thread-Safe 하지 않음)
    private final List<Session> sessions = new ArrayList<>();

    // 공정성(Fairness) 정책은 false(기본값)로 설정하여 처리량(Throughput) 우선
    // 필요한 경우 new ReentrantLock(true)로 설정 가능하나 성능 저하 발생 가능
    private final Lock lock = new ReentrantLock();

    /**
     * 세션 추가
     * * @param session 추가할 세션 객체
     */
    public void add(Session session) {
        lock.lock(); // 락 획득
        try {
            sessions.add(session);
        } finally {
            // 예외 발생 여부와 관계없이 반드시 락 해제
            lock.unlock();
        }
    }

    /**
     * 세션 제거
     * * @param session 제거할 세션 객체
     */
    public void remove(Session session) {
        lock.lock();
        try {
            sessions.remove(session);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 모든 세션 종료
     * [성능 최적화 포인트]
     * 세션의 close() 메서드는 네트워크 I/O를 동반할 가능성이 높음.
     * 락을 잡은 상태에서 반복문을 돌며 close()를 호출하면,
     * 해당 작업이 끝날 때까지 다른 스레드가 add/remove를 할 수 없음(Bottleneck).
     * 따라서 현재 세션 리스트를 복사(Snapshot)하고 원본을 비운 뒤 락을 해제하고,
     * 복사본을 통해 락 없이 종료 작업을 수행해야 함.
     */
    public void closeAll() {
        List<Session> sessionsCopy;

        lock.lock();
        try {
            // 현재 관리 중인 세션들의 스냅샷 생성 (매우 빠른 메모리 연산)
            sessionsCopy = new ArrayList<>(sessions);

            // 원본 리스트 클리어
            sessions.clear();
        } finally {
            lock.unlock();
        }

        // 3. 락 범위 밖에서 실제 I/O 작업(종료) 수행
        // 이를 통해 I/O 작업 중에도 다른 스레드는 add/remove 가능
        for (Session session : sessionsCopy) {
            session.close();
        }
    }

    public void sendAll(String message) {
        
        lock.lock();

        try {
            for (Session session : sessions) {
                try {
                    session.send(message);
                } catch (IOException e) {
                    log(e);
                }
            }
        } finally {
            lock.unlock();
        }
        
    }
    
    public List<String> getAllUsername() {
        
        lock.lock();
        
        try {
            
            List<String> usernames = new ArrayList<>();
            
            for (Session session : sessions) {
                if (session.getUsername() != null) {
                    usernames.add(session.getUsername());
                }
            }
            
            return usernames;
        } finally {
            lock.unlock();
        }
    } 
}
