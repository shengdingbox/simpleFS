package com.zhouzifei.tool.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 周子斐
 * @date 2021/2/1
 * @Description
 */
@Data
public class HttpResponses {
    Map<String, List<String>> map = new HashMap<>();

    String body = "";
}
