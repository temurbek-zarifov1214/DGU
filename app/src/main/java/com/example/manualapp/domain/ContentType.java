package com.example.manualapp.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Content sections for the "Amaliy bezaklar sanati" application.
 * Each value maps a home-screen card to its PDF assets bundled under app/src/main/assets.
 * Enum order == home grid order (see MainActivity.TYPES and ShowItemsActivity.SUBTITLES).
 */
public enum ContentType {

    // 0 ── Naqshlar tarix tilsimi ──────────────────────────────────────────────
    TARIX(
            "Naqshlar tarix tilsimi",
            "Naqshlar tarix tilsimi",
            Arrays.asList(
                    "I. Me'moriy yodgorlikdagi naqshlar olami",
                    "II. Me'moriy obidalar va davrlar silsilasi",
                    "III. Islom naqqoshlik san'ati"),
            new PdfPathStrategy() {
                private final String[] paths = {
                        "Naqshlar tarix tilsimi/I. Me'moriy yodgorlikdagi naqshlar olami.pdf",
                        "Naqshlar tarix tilsimi/II. Me'moriy obidalar va davrlar silsilasi.pdf",
                        "Naqshlar tarix tilsimi/III. Islom naqqoshlik san'ati va 'Abadiy' chiziqlar tahlili.pdf"
                };
                @Override
                public String getPath(int position) {
                    return position >= 0 && position < paths.length ? paths[position] : paths[0];
                }
            }),

    // 1 ── Naqqoshlik uslublar olami ───────────────────────────────────────────
    USLUBLAR(
            "Naqqoshlik uslublar olami",
            "Naqqoshlik uslublar olami",
            Arrays.asList(
                    "Buxoro maktabi",
                    "Farg'ona maktabi",
                    "Samarqand maktabi",
                    "Toshkent maktabi",
                    "Xiva maktabi"),
            new PdfPathStrategy() {
                private final String[] paths = {
                        "Naqqoshlik uslublar olami/Buxoro maktabi.pdf",
                        "Naqqoshlik uslublar olami/Farg'ona maktabi.pdf",
                        "Naqqoshlik uslublar olami/Samarqand maktabi.pdf",
                        "Naqqoshlik uslublar olami/Toshkent maktabi.pdf",
                        "Naqqoshlik uslublar olami/Xiva maktabi.pdf"
                };
                @Override
                public String getPath(int position) {
                    return position >= 0 && position < paths.length ? paths[position] : paths[0];
                }
            }),

    // 2 ── Naqsh chizish asoslari ──────────────────────────────────────────────
    ASOSLAR(
            "Naqsh chizish asoslari",
            "Naqsh chizish asoslari",
            Collections.singletonList("Shakllar asosida naqsh chizish"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "Naqsh chizish asoslari/Shakllar asosida naqsh chizish.pdf";
                }
            }),

    // 3 ── Naqshlar ilhom manbai ───────────────────────────────────────────────
    ILHOM(
            "Naqshlar ilhom manbai",
            "Naqshlar ilhom manbai",
            Collections.singletonList("Naqsh kompozitsiyasidan namunalar"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "Naqshlar ilhom manbai/Naqsh kompozitsiyasidan namunalar.pdf";
                }
            }),

    // 4 ── Asbob va materiallar ────────────────────────────────────────────────
    ASBOB(
            "Asbob va materiallar",
            "Asbob va materiallar",
            Arrays.asList(
                    "Akvarel, guash, tempera",
                    "Axta",
                    "Chizg'ich, uchburchakliklar",
                    "Kalka",
                    "Mo'yqalamlar",
                    "Qalamlar",
                    "Qog'oz",
                    "Sirkul",
                    "Xoka"),
            new PdfPathStrategy() {
                private final String[] paths = {
                        "Asbob va materiallar/Akvarel, guash, tempera.pdf",
                        "Asbob va materiallar/Axta.pdf",
                        "Asbob va materiallar/Chizg'ich, uchburchakliklar.pdf",
                        "Asbob va materiallar/Kalka.pdf",
                        "Asbob va materiallar/Mo'yqalamlar.pdf",
                        "Asbob va materiallar/Qalamlar.pdf",
                        "Asbob va materiallar/Qog'oz.pdf",
                        "Asbob va materiallar/Sirkul.pdf",
                        "Asbob va materiallar/Xoka.pdf"
                };
                @Override
                public String getPath(int position) {
                    return position >= 0 && position < paths.length ? paths[position] : paths[0];
                }
            }),

    // 5 ── Muallif haqida ──────────────────────────────────────────────────────
    MUALLIF(
            "Muallif haqida",
            "Muallif haqida",
            Collections.singletonList("Xudoyberdiyeva Xulkar Zoxid qizi"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "Muallif haqida/Xudoyberdiyeva Xulkar Zoxid qizi.pdf";
                }
            }),

    // 6 ── Ilovadan yo'riqnomasi ───────────────────────────────────────────────
    YORIQNOMA(
            "Foydalanish yo'riqnomasi",
            "Foydalanish yo'riqnomasi",
            Collections.singletonList("Ilovadan foydalanish yo'riqnomasi"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "Ilovadan yo'riqnomasi/Ilovadan foydalanish yo'riqnomasi.pdf";
                }
            }),

    // 7 ── DGU (ilova hujjati) ─────────────────────────────────────────────────
    DGU(
            "Ilova hujjati",
            "Amaliy bezaklar sanati",
            Collections.singletonList("DGU Amaliy bezak sanati"),
            new PdfPathStrategy() {
                @Override
                public String getPath(int position) {
                    return "DGU/DGU Amaliy bezak sanati.pdf";
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
}
