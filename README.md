carelink-android-(current)master
================

Communication between CareLink USB stick and Android using the USB Host API.

The following gist shows the bytearray response from the stick using simple commands:

https://gist.github.com/marksvdev/d179729faeef3d66d4b0

In the screenshot shown below the response from the opcode [ 0x04, 0x00 ] in non-decoded-toString form:

![com1](http://i.imgur.com/CzHlNWY.png)


carelink-android-prototype
===============

Currently working on branch carelink-android-prototype.

Interfacing between Java and Javascript/JockeyJS in a WebView. WebViewHandler.java will handle all communication between Jockey and CareLink stick using CareLinkUsb.java.

The following diagram shows a rough sketch of what I'm trying to accomplish:

![sketch](http://i.imgur.com/s5marpg.png)

Basically trying to move most of the control from Java (see current master branch) to Jockey. Only using Java/Android as a passthrough to the stick using it's Usb Host API.
