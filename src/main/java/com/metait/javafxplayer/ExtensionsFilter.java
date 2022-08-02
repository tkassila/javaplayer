package com.metait.javafxplayer;

import java.io.File;
import java.io.FileFilter;

public class ExtensionsFilter implements FileFilter
{
    // private char[][] extensions;
    private String[] extensions;

    public ExtensionsFilter(String[] extensions)
    {
        int length = extensions.length;
        // this.extensions = new char[length][];
        this.extensions = new String[length];
        for (String s : extensions)
        {
            //this.extensions[--length] = s.toCharArray();
            this.extensions[--length] = s.toLowerCase();
        }
        System.out.println("");
    }

    @Override
    public boolean accept(File file)
    {
        String name = file.getName();
        if (name == null || name.trim().length() == 0)
            return false;
        for(String ext : extensions)
            if (name.endsWith(ext))
                return true;
        return false;
            /*
            char[] path = file.getPath().toCharArray();
            for (char[] extension : extensions)
            {
                if (extension.length > path.length)
                {
                    continue;
                }
                int pStart = path.length - 1;
                int eStart = extension.length - 1;
                boolean success = true;
                for (int i = 0; i <= eStart; i++)
                {
                    if ((path[pStart - i] | 0x20) != (extension[eStart - i] | 0x20))
                    {
                        success = false;
                        break;
                    }
                }
                if (success)
                    return true;
            }
            return false;
             */
    }
}
