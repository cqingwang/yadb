chcp 65001
adb push yadb /data/local/tmp
adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.sogou.yarn.Main -keyboard 你好，世界
pause