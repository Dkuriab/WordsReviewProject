package org.controllers;

public class Language {
    public String realName;
    public String TranslateName;

    public Language(String realName, String TranslateName) {
        this.realName = realName;
        this.TranslateName = TranslateName;
    }

    @Override
    public String toString() {
        return realName;
    }
}
