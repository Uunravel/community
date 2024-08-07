package com.nowcoder.community;

import java.io.IOException;

public class WKTests {
    public static void main(String[] args) {
        String cmd = "D:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltoimage --quality 75 https://www.nowcoder.com D:\\Code\\Java\\workspace\\data\\wk-images\\2.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("OK");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
