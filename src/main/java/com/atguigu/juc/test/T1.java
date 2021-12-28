package com.atguigu.juc.test;

public class T1 {
    public static void main(String[] args) {
        Book book = new Book();
        Book book1 = book.setId(11).setBookName("MYSQL").setAuthor("z3").setPrice(28.3);
    }
}
