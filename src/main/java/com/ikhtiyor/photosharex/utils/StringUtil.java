package com.ikhtiyor.photosharex.utils;

public final class StringUtil {

    public static String formatItemAddMessage(Integer itemSize) {
        if (itemSize == 1) {
            return String.format("%d Item added", itemSize);
        }

        return String.format("%d Items added", itemSize);
    }
}
