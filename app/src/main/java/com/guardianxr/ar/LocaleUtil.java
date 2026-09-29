package com.guardianxr.ar;
import android.content.*;import android.content.res.Configuration;import java.util.Locale;
public final class LocaleUtil{
 private LocaleUtil(){}
 public static Context apply(Context c){String code=c.getSharedPreferences("guardianxr_lang",Context.MODE_PRIVATE).getString("language","en");Locale l=new Locale(code);Locale.setDefault(l);Configuration cfg=new Configuration(c.getResources().getConfiguration());cfg.setLocale(l);return c.createConfigurationContext(cfg);}
 public static void save(Context c,String code){c.getSharedPreferences("guardianxr_lang",Context.MODE_PRIVATE).edit().putString("language",code).apply();}
 public static String current(Context c){return c.getSharedPreferences("guardianxr_lang",Context.MODE_PRIVATE).getString("language","");}
}