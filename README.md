# javaplayer

The JavaPlayer is capable to play 
different kind of audio and video files or directories,
daisy 2 books. UI of the application has accessible buttons etc, 
which are working with screen readers. many buttons also has 
keyboard keys to press and control.

When application is started, press some of short keys:
- alt+f = open audio etc file, 
- alt+d = open daisy book ncc.html file
- alt+m = open music directory and play audio files

### Daisy book features

User bookmarks buttons are now implemented. Now application is storing last selected directory or mp file or daisy html file into configuration files under user home directory.

### Play user interface

1. Player has menu row: File (implemented), Edit, Help menus
2. From Start = play from start, beginning of file
3. Previous or next audio file
4. Prev index link or Next index link = during daisy books is finding prev or next link in index html page below of application
5. Select lower one from index levels-, Select upper one from index levels- buttons: by ex. 1,2,3 or 4
6. Prev index inside of selected level- and Next index inside of selected level-button

Buttons 4 - 6 are corresponds buttons existing on Daisy 2 device like Victor etc.

On volume control + key is playing louder sound and - key means quieter sound.
Behind Help button is a dislog showing more comprehend help page content.

### Player file types

This java player can play or show next kind of files:

.mp3,  .html, .htm,  .wav, .mp4, .aiff,  .aif,  .aifc,  .m4a,  .m4b,  .m4p,  .m4r,  .m4v,  .3gp, 
.flv,  .f4v,  .f4p,  .f4a,  .f4b
