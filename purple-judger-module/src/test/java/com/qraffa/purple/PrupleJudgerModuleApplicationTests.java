package com.qraffa.purple;

import com.qraffa.purple.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;

@SpringBootTest
class PrupleJudgerModuleApplicationTests {

    @Test
    void contextLoads() throws Exception {
        String src = "/home/qraffa/purple-docker/data/1.zip";
        String dest = "/home/qraffa/purple-docker/data/1";
        FileUtil.unzip(Paths.get(src), dest);
    }

}
