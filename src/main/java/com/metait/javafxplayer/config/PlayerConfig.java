package com.metait.javafxplayer.config;

import com.google.gson.Gson;
import com.metait.javafxplayer.PlayerController;
import com.metait.javafxplayer.bookmark.BookMark;
import com.metait.javafxplayer.bookmark.BookMarkCollection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class PlayerConfig {
    public BookMarkCollection [] userBookmarks;
    public String userRadiobuttonSelected;
    public BookMark autoBookmark;
    public Locale userSelectedLocale;

    private static String java_user_home = System.getProperty("user.home");

    public static Locale getLanguageLocale()
    {
        File appUserDir = new File(java_user_home +File.separator +PlayerController.cnstUserDirOfApp);
        if (!appUserDir.exists())
            if (!appUserDir.mkdir())
                return null;
        File appFile = new File(appUserDir.getAbsolutePath() +File.separator +PlayerController.cnstUserJsonFileOfApp);
        if (!appFile.exists())
            return null;

        Locale locale = new Locale("en", "EN");
        List<String> listContent = null;
        String strJson = null;
        PlayerConfig config = null;
        try {
            try {
                listContent = Files.readAllLines(Paths.get(appFile.getAbsolutePath()));
            }catch (IOException ioe2){
                if (ioe2.getMessage().contains("Input length = 1"))
                    listContent = Files.readAllLines(Paths.get(appFile.getAbsolutePath()), StandardCharsets.ISO_8859_1);
                else
                    throw ioe2;
            }

            if (listContent != null && listContent.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (String line : listContent)
                    sb.append(line);
                strJson = sb.toString();
            }
            if (strJson == null || strJson.trim().length()==0)
                return null;

            Gson gson = new Gson();
            config = gson.fromJson(strJson, PlayerConfig.class);
            if (config != null)
            {
                Locale tmp_locale = config.userSelectedLocale;
                if (tmp_locale != null) {
                    locale = tmp_locale;
                    PlayerController.locale = tmp_locale;
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            System.err.println("Reading error in file: " +appFile.getAbsolutePath() +" " +ioe.getMessage());
        }
        return locale;
    }

}
