package charset;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AvailableCharsetsMain {
    public static void main(String[] args) {
        // 이용 가능한 모든 Charset 자바 + OS
        Charset.availableCharsets().forEach((k, v) -> System.out.println("charsetName : " + v));

        System.out.println("=====");
        // 문자로 조회 (대소문자 구분X)
        Charset charset1 = Charset.forName("ms949");
        System.out.println(charset1);
        
        // 별칭 조회
        charset1.aliases().forEach((k) -> System.out.println("charSet Alias : " + k));
        
        // UTF-8 문자로 조회
        Charset charset2 = StandardCharsets.UTF_8;
        System.out.println("charset2 : " + charset2);
        
        // 시스템의 기본 Charset 조회
        Charset defaultCharset = Charset.defaultCharset();
        System.out.println("defaultCharset : " + defaultCharset);
    }
}
