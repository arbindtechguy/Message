package com.example.arbindtechguy.message;

import android.widget.Toast;

/**
 * Created by arbindtechguy on 11/3/18.
 */

public class Messages {
    private String message,type;
    private long time;
    private Boolean seen;

    static char[] chars = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@',
            '#', '$', '%', '^', '&', '(', ')', '+',
            '-', '*', '/', '[', ']', '{', '}', '=',
            '<', '>', '?', '_', '"', '.', ',', ' '
    };
    private int offset = 2;




    public Messages(){
    }

    public Messages(String message, String type, long time, Boolean seen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
       // this.message = message;

        String dec = decrypt(message, offset);
        this.message = dec;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }






    static String decrypt(String cip, int offset)
    {
        char[] cipher = cip.toCharArray();
        for (int i = 0; i < cipher.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j >= offset && cipher[i] == chars[j]) {
                    cipher[i] = chars[j - offset];
                    break;
                }
                if (cipher[i] == chars[j] && j < offset) {
                    cipher[i] = chars[(chars.length - offset +1) + j];
                    break;
                }
            }
        }
        return String.valueOf(cipher);
    }

}
