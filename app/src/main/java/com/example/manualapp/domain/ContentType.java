package com.example.manualapp.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ContentType {
    MARUZA(
            "Maruzadan matni",
            "Maruzadan matnlari",
            buildMaruzaList(),
            new PdfPathStrategy() {
                private final String[] paths = {
                        "maruza/1-ma'ruza.pdf",
                        "maruza/2-mavzu.pdf",
                        "maruza/3-ma'ruza.pdf",
                        "maruza/4-ma'ruza.pdf",
                        "maruza/5-ma'ruza.pdf",
                        "maruza/6-ma'ruza.pdf",
                        "maruza/7-ma'ruza.pdf",
                        "maruza/8-ma'ruza.pdf",
                        "maruza/9-ma'ruza.pdf",
                        "maruza/10-ma'ruza.pdf",
                        "maruza/11-ma'ruza.pdf",
                        "maruza/12-ma'ruza.pdf",
                        "maruza/13-ma'ruza.pdf",
                        "maruza/14-ma'ruza.pdf",
                        "maruza/15-ma'ruza.pdf",
                        "maruza/16-ma'ruza.pdf"
                };
                @Override
                public String getPath(int position) {
                    return position >= 0 && position < paths.length ? paths[position] : paths[0];
                }
            }),
    AMALIYOT(
            "Amalliy mashg'ulotlar",
            "Amaliyotdan mavzular",
            buildAmaliyotList(),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    if (position < 10) {
                        return "amaliyot/" + (position + 1) + "-amaliy.pdf";
                    } else {
                        return "amaliyot/" + (position + 1) + "-mavzu.pdf";
                    }
                }
            }),
    TARQATMA(
            "Fan dastur",
            "Fan dastur",
            Collections.singletonList("Fan dastur"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "Fan dastur/Fan dastur.PDF";
                }
            }),
    GLOSSARY(
            "Glossary",
            "Glossary",
            Collections.singletonList("Glossary"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "Glossary/GLOSSARIY.pdf";
                }
            }),
    ORALIQ(
            "Oraliq savollar",
            "Oraliq savollar",
            Collections.singletonList("Oraliq test savollar"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "oraliqtest/Oraliq test.pdf";
                }
            }),
    YAKUNIY(
            "Yakuniy savollar",
            "Yakuniy savollar",
            Collections.singletonList("Test savollar"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "yakuniytest/Yakuniy nazorat uchun test 200 ta.pdf";
                }
            }),
    DGU(
            "Mobil ilova hujjati",
            "Zamonaviy ta'lim texnologiyalar fani",
            Collections.singletonList("Zamonaviy ta'lim texnologiyalari fani android mobil ilovasi"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "dgu/Mobile ilova hujjati.pdf";
                }
            }),
    MALUMOTNOMA(
            "Mualiflar haqida",
            "Dastur muallifi",
            Arrays.asList("Sohibov Akram Rustamovich", "Nabiyeva"),
            new PdfPathStrategy() {
                private final String[] paths = {"malumotnoma/Ma'lumotnoma Soxibov.PDF", "malumotnoma/Ma'lumotnoma Nabiyeva.pdf"};
                @Override
                public String getPath(int position) {
                    return position >= 0 && position < paths.length ? paths[position] : paths[0];
                }
            });

    public static final String KEY = "content_type";

    private final String screenTitle;
    private final String toolbarTitle;
    private final List<String> itemNames;
    private final PdfPathStrategy pdfPathStrategy;

    ContentType(String screenTitle, String toolbarTitle, List<String> itemNames, PdfPathStrategy pdfPathStrategy) {
        this.screenTitle = screenTitle;
        this.toolbarTitle = toolbarTitle;
        this.itemNames = itemNames;
        this.pdfPathStrategy = pdfPathStrategy;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public String getToolbarTitle() {
        return toolbarTitle;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public String getPdfPath(int position) {
        return pdfPathStrategy.getPath(position);
    }

    public interface PdfPathStrategy {
        String getPath(int position);
    }

    private static List<String> buildMaruzaList() {
        List<String> list = new ArrayList<>(16);
        for (int i = 1; i <= 16; i++) {
            list.add(i + "-Maruza");
        }
        return list;
    }

    private static List<String> buildAmaliyotList() {
        List<String> list = new ArrayList<>(20);
        for (int i = 1; i <= 20; i++) {
            list.add("Amaliy mashg'ulot - " + i);
        }
        return list;
    }
}
