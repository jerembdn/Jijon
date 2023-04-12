package com.onruntime.jijon.util;

public class FormatMessage {
  public static String format(String prefix, String text, Object ...args) {
    return String.format("§6§l%s§r §f§l|§r " + String.format(text, args), prefix);
  }

  public static String error(String text, Object ...args) {
    return String.format("§c§l❌§r §f§l|§r " + String.format(text, args));
  }
}
