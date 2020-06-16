package com.qraffa.pruplejudgermodule.util.judge;

import com.qraffa.pruplejudgermodule.util.rpc.JsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单个测试样例中包含的信息
 * @author qraffa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeTestCase extends JsonSerializer {

    private String inputName;

    private String outputName;

    private String outputMd5;

}
