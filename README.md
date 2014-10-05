caredroid-interface-htmlui
===============

On branch carelink-android-prototype_wip the app is working to comply with the diagram below.

![sketch](http://i.imgur.com/s5marpg.png)

This demonstrates that jockeyJS and the code within, can control the CareLink dongle through the android Host API. We will be working on adapting the javascript interface - using jockeyJS - so existing code, provided by @bewest, can interface with the CareLink stick using the implemented android usb library.

Take note of index.html and WebViewHandler.java, as those are the files we will be focusing on.

###Changes from caredroid-interface_wip
- Moved webview with JockeyJS from WebviewHandler.java (Service) to MainActivity.java (Activity).
- Removed Android UI components and replaced by the (now visible) jockey WebView.
- GUI can now be written entirely in HTML and javascript.

Example below with a html button and scrollable div.
![ex](http://i.imgur.com/MxwlGRf.png)
