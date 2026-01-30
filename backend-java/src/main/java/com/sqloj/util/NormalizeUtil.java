package com.sqloj.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 结果集标准化工具类
 * 用于消除字段顺序差异，确保判题准确性
 */
public class NormalizeUtil {

    /**
     * 标准化结果集
     * @param resultJson 结果集JSON字符串（二维数组格式）
     * @param headers 字段名数组
     * @return 标准化后的结果集（字段按字母序排序）
     */
    public static List<Map<String, Object>> normalize(String resultJson, String[] headers) {
        if (resultJson == null || headers == null || headers.length == 0) {
            return new ArrayList<>();
        }

        try {
            JSONArray jsonArray = JSON.parseArray(resultJson);
            List<Map<String, Object>> normalizedList = new ArrayList<>();

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray row = jsonArray.getJSONArray(i);
                Map<String, Object> rowMap = new TreeMap<>(); // TreeMap自动按字母序排序

                for (int j = 0; j < headers.length && j < row.size(); j++) {
                    rowMap.put(headers[j], row.get(j));
                }

                normalizedList.add(rowMap);
            }

            return normalizedList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 从结果集中提取并标准化
     * @param resultList 原始结果集（List<Map>格式）
     * @return 标准化后的结果集
     */
    public static List<Map<String, Object>> normalizeFromList(List<Map<String, Object>> resultList) {
        if (resultList == null || resultList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> normalizedList = new ArrayList<>();
        for (Map<String, Object> row : resultList) {
            Map<String, Object> normalizedRow = new TreeMap<>();
            normalizedRow.putAll(row);
            normalizedList.add(normalizedRow);
        }

        return normalizedList;
    }

    /**
     * 比较两个标准化后的结果集是否相等
     * @param result1 结果集1
     * @param result2 结果集2
     * @return 是否相等
     */
    public static boolean equals(List<Map<String, Object>> result1, List<Map<String, Object>> result2) {
        if (result1 == null || result2 == null) {
            return result1 == result2;
        }

        if (result1.size() != result2.size()) {
            return false;
        }

        for (int i = 0; i < result1.size(); i++) {
            Map<String, Object> row1 = result1.get(i);
            Map<String, Object> row2 = result2.get(i);

            if (!mapsEqual(row1, row2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 比较两个Map是否相等
     */
    private static boolean mapsEqual(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1.size() != map2.size()) {
            return false;
        }

        for (String key : map1.keySet()) {
            Object val1 = map1.get(key);
            Object val2 = map2.get(key);

            if (!objectEquals(val1, val2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 比较两个对象是否相等（处理null和类型转换）
     */
    private static boolean objectEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }

        // 转换为字符串进行比较（避免类型差异）
        String str1 = String.valueOf(obj1);
        String str2 = String.valueOf(obj2);

        return str1.equals(str2);
    }

    /**
     * 计算结果集差异
     * @param standardResult 标准结果集
     * @param actualResult 实际结果集
     * @return 差异信息 [缺失行数, 错误行数]
     */
    public static int[] calculateDifference(List<Map<String, Object>> standardResult, 
                                           List<Map<String, Object>> actualResult) {
        int lackNum = 0;
        int errNum = 0;

        if (standardResult == null || actualResult == null) {
            return new int[]{0, 0};
        }

        // 缺失行数
        if (actualResult.size() < standardResult.size()) {
            lackNum = standardResult.size() - actualResult.size();
        }

        // 错误行数
        int minSize = Math.min(standardResult.size(), actualResult.size());
        for (int i = 0; i < minSize; i++) {
            if (!mapsEqual(standardResult.get(i), actualResult.get(i))) {
                errNum++;
            }
        }

        return new int[]{lackNum, errNum};
    }
}
