
-keep class com.mdtech.jencenterjar.Admanager {
  public <methods>;#保持该类下所有的共有方法不被混淆
  }
-keep class com.mdtech.jencenterjar.DataBean{ *; }

-keep class com.mdtech.jencenterjar.DataTestBean {
  public <methods>;#保持该类下所有的共有方法不被混淆
  }

  -keep class com.mdtech.jencenterjar.utils.** { *; }