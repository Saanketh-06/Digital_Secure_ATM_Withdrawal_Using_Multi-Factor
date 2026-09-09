package com.atm.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Central place for fonts, colors, and small widget factories used across screens. */
public final class UIStyle {

    public static final Color PRIMARY = new Color(0x0B5FFF);
    public static final Color PRIMARY_DARK = new Color(0x083D9E);
    public static final Color SUCCESS = new Color(0x1E8E3E);
    public static final Color DANGER = new Color(0xD93025);
    public static final Color BG = new Color(0xF4F6FA);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(0x6B7280);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_MONO_BALANCE = new Font("Consolas", Font.BOLD, 30);

    private UIStyle() { }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 22, 10, 22));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(PRIMARY, 1, true));
        return button;
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(new Color(0x1A1A1A));
        return label;
    }

    public static JLabel subtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE3E6EC), 1, true),
                new EmptyBorder(28, 32, 28, 32)));
        return panel;
    }
}
