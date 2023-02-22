package com.metait.javafxplayer;

import java.io.File;

public interface IFileContainer {
    public void callUpdateSplitPaneDividerPosition(boolean bValue, String metadata);
    public boolean isVideoFile(File f);
    public NewSelectedFile getPossibleNextPlayfile();
}
