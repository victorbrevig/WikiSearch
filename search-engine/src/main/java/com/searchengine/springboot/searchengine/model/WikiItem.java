package com.searchengine.springboot.searchengine.model;

public class WikiItem {
    String str;
    WikiItem next;

    WikiItem(String s, WikiItem n) {
        str = s;
        next = n;
    }
}
