package com.example.manualapp.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ContentType {
    MARUZA(
            "Maruzadan matni",
            "Maruzadan matnlari",
            Arrays.asList("1-Maruza", "2-Maruza", "3-Maruza", "4-Maruza", "5-Maruza", "6-Maruza", "7-Maruza"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "maruza/maruza-" + (position + 1) + ".pdf";
                }
            }),
    AMALIYOT(
            "Amalliy mashg'ulotlar",
            "Amaliyotdan mavzular",
            buildAmaliyotList(),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "amaliyot/amaliy-" + (position + 1) + ".pdf";
                }
            }),
    TARQATMA(
            "Tarqatma materiallar",
            "Tarqatma materiallar",
            Collections.singletonList("Tarqatma materiallar"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "labaratoriya/TARQATMA MATERIALLAR.pdf";
                }
            }),
    GLOSSARY(
            "Glossary",
            "Glossary",
            Collections.singletonList("Glossary"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "qollamma/Glossary.pdf";
                }
            }),
    ORALIQ(
            "Oraliq savollar",
            "Oraliq savollar",
            Collections.singletonList("Oraliq test savollar"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "oraliqtest/Test savollari ON.pdf";
                }
            }),
    YAKUNIY(
            "Yakuniy savollar",
            "Yakuniy savollar",
            Collections.singletonList("Test savollar"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "yakuniytest/Test savollari YN.pdf";
                }
            }),
    DGU(
            "Mobil ilova hujjati",
            "Komparativistik pedagogika fanini o'gatuvchi",
            Collections.singletonList("Komparativistik pedagogika fanini o'gatuvchi\" android mobil ilovasi"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "dgu/DGU.pdf";
                }
            }),
    MALUMOTNOMA(
            "Mualiflar haqida",
            "Dastur muallifi",
            Arrays.asList("Sohibov Akram Rustamovich", "Salimova Marjona Abduraxim qizi"),
            new PdfPathStrategy() {
                private final String[] paths = {"malumotnoma/Ma'lumotnoma Soxibov.PDF", "malumotnoma/obektivka Salimova.pdf"};
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

    private static List<String> buildAmaliyotList() {
        List<String> list = new ArrayList<>(15);
        for (int i = 1; i <= 15; i++) {
            list.add("Amaliy mashg'ulot - " + i);
        }
        return list;
    }
}
