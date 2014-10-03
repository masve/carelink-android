carelink-android-prototype_wip
===============

Currently working on branch carelink-android-prototype.

Interfacing between Java and Javascript/JockeyJS in a WebView. WebViewHandler.java will handle all communication between Jockey and CareLink stick using CareLinkUsb.java.

The following diagram shows a rough sketch of what I'm trying to accomplish:

![sketch](http://i.imgur.com/s5marpg.png)

Basically trying to move most of the control from Java (see current master branch) to Jockey. Only using Java/Android as a passthrough to the stick using its Usb Host API.


This branch was a proof of concept to demonstrate the above sketch. This is now implemented, and we will move forward on another branch.
