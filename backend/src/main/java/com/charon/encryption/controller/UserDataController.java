package com.charon.encryption.controller;

import com.charon.encryption.annotation.EncryptResponse;
import com.charon.encryption.reponse.AjaxResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * form表单接口类
 *
 * @author charon
 * @date 2025/3/31 21:58
 */
@EncryptResponse
@RestController
@RequestMapping("/userData")
public class UserDataController {

    @PostMapping("/submit-form")
    public AjaxResult submitForm(@RequestBody Map<String, Object> formData) {
        return AjaxResult.success(formData);
    }

    @GetMapping("/getUserData")
    public AjaxResult getUserData(@RequestParam String userId) throws IOException {
        System.out.printf("getUserData userId=%s\n", userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("name", "Charon");
        userData.put("gender", "1");
        userData.put("age", 20);
        userData.put("address", "China");
        userData.put("hobby", Arrays.asList("singing", "basketball"));
        userData.put("desc", "备注信息");

        return AjaxResult.success(userData);
    }

}
