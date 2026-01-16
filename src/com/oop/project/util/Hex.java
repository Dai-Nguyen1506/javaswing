package com.oop.project.util;

public final class Hex {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private Hex() {
    }

    public static String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = HEX[v >>> 4];
            out[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(out);
    }

    public static byte[] fromHex(String hex) {
        String s = hex.trim();
        if ((s.length() % 2) != 0) {
            throw new IllegalArgumentException("Invalid hex length");
        }
        byte[] out = new byte[s.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(s.charAt(i * 2), 16);
            int lo = Character.digit(s.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                throw new IllegalArgumentException("Invalid hex character");
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}
