Trustly Library Android v1.0
=====================

This library has been created for Android to simplify the process of
implementing the functionality needed when using Trustly within an Android app.

For full documentation on the Trustly API internals visit our developer
website: http://trustly.com/developer .

Overview
========

JAVASCRIPTINTERFACE
Android WebView's are not permitted to open external applications with the use of URL schemes.
It is therefore essential to implement a JavascriptInterface. This can be done by importing
this library to you project using "File -> New -> Import Module..." in Android Studio.
Don't forget to add the library to you project dependencies. Also make sure that javascript 
and DOM Storage is enabled since they are disabled by default in Android WebView's.

Example
--------------------

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Get WebView & WebSettings
    WebView myWebView = (WebView) findViewById(R.id.webview);
    myWebView.setWebViewClient(new WebViewClient());
    WebSettings webSettings = myWebView.getSettings();

    // Enable javascript and DOM Storage
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);

    // Add TrustlyJavascriptInterface
    myWebView.addJavascriptInterface(
    new TrustlyJavascriptInterface(this), TrustlyJavascriptInterface.NAME);
}
