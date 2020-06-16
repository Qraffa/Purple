package com.qraffa.pruplejudgermodule.util.judge;

import com.qraffa.pruplejudgermodule.util.rpc.JsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 判题数据信息文件
 * @author qraffa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeInfo extends JsonSerializer {

    private List<JudgeTestCase> testCases;

}
