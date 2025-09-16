package io.member.impl;

import io.member.Member;
import io.member.MemberRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileMemberRepository implements MemberRepository {

    private static final String FILE_PATH = "temp/member-text.dat";
    private static final String DELIMITER = ",";
    
    @Override
    public void add(Member member) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, UTF_8, true));) {
            bw.write(member.getId() + DELIMITER + member.getName() + DELIMITER + member.getAge());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH, UTF_8))) {
           String line;
           while ((line = br.readLine()) != null) {
               String[] memberData = line.split(DELIMITER);
               members.add(new Member(memberData[0], memberData[1], Integer.parseInt(memberData[2])));
           }
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return members;
    }
}
