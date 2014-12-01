package com.lucass;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String msg = "NOSSO ENDEREÇO IP = ";
        if(args.length > 0) {
            msg += args[0];
        } else {
            msg += "NULL";
        }
        System.out.println(msg);
    }
}
