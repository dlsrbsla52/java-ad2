package test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

public class CreateHugeExcelDummy {

    // ==========================================
    // 설정 영역
    // ==========================================
    // 생성할 파일 경로 (.xlsm 확장자 사용)
    // Windows 예시: "C:/Temp/large_test_file.xlsm"
    // Mac/Linux 예시: "/home/user/data/large_test_file.xlsm"
    // **주의: 해당 드라이브에 100GB 이상의 여유 공간이 있어야 합니다.**
    private static final String FILE_PATH = "/Users/hig/filetest";

    // 목표 크기 (GB 단위)
    private static final long TARGET_SIZE_GB = 100L;

    // 버퍼 크기 (8MB)
    private static final int BUFFER_SIZE = 8 * 1024 * 1024;
    // ==========================================

    public static void main(String[] args) {
        long targetSizeBytes = TARGET_SIZE_GB * 1024L * 1024L * 1024L;

        System.out.println("=== 100GB .xlsm 더미 파일 생성 시작 ===");
        System.out.printf("저장 경로: %s%n", FILE_PATH);
        System.out.printf("목표 크기: %d GB (%,d Bytes)%n", TARGET_SIZE_GB, targetSizeBytes);
        System.out.println("---------------------------------------");

        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 더미 데이터 생성 ('A' 문자로 채움)
        byte[] buffer = new byte[BUFFER_SIZE];
        Arrays.fill(buffer, (byte) 'A');

        long startTime = System.currentTimeMillis();
        long totalWritten = 0;
        DecimalFormat df = new DecimalFormat("#.##");

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FILE_PATH))) {

            while (totalWritten < targetSizeBytes) {
                long remaining = targetSizeBytes - totalWritten;
                int bytesToWrite = (int) Math.min(BUFFER_SIZE, remaining);

                bos.write(buffer, 0, bytesToWrite);
                totalWritten += bytesToWrite;

                // 1GB 단위로 진행 상황 출력 (너무 잦은 출력 방지)
                if (totalWritten % (1024L * 1024L * 1024L) < BUFFER_SIZE) {
                    double percent = (double) totalWritten / targetSizeBytes * 100.0;
                    System.out.printf("진행률: %s%% 완료 (%,d Bytes)%n", df.format(percent), totalWritten);
                }
            }

            bos.flush();

        } catch (IOException e) {
            System.err.println("\n[오류] 파일 생성 중 문제가 발생했습니다.");
            System.err.println("디스크 공간이 부족하거나 권한이 없는지 확인해주세요.");
            e.printStackTrace();
            return;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("---------------------------------------");
        System.out.println("=== 파일 생성 완료 ===");
        System.out.printf("총 소요 시간: %d 초%n", (endTime - startTime) / 1000);
        System.out.printf("파일 크기: %,d Bytes%n", file.length());
        System.out.println("참고: 이 파일은 실제 엑셀 포맷이 아니므로 엑셀에서 열 수 없습니다. (업로드 테스트용)");
    }
}