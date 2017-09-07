#### 2017.5.20

- 删除文件报Clear Read-Only Status，然后点击ok，就卡死了，然后发现所有module下的build文件夹都有一个小锁的标志；

**已解决** ：因为在进行codecc进行编译时，使用了sudo python build.py QCloudAndroidCore，由于使用了超级用户权限，导致两次编译时是不同的权限，结果。。

解决方式：在终端运行

```
sudo chown -R $USER 工程主目录
```
然后重新进行编译即可。


#### 2017.5.22

- 在QCloudHttpRequstOrigin的build方法中，如果传入的RequestBodySerializer参数为空，竟然没有报空指针异常，很奇怪，目前初步断定是在空指针调用处抛出的异常被上层调用方法捕获。

目前还未找到原因，只是加了if来进行异常规避。

原因是Callable接口中的Call方法捕获了异常，且未抛出。想要获得Call方法抛出的异常，需要调用Future.get()方法


#### 2017.5.24

- ```    compile ('com.thoughtworks.xstream:xstream:1.4.7') {
        exclude group: 'xmlpull', module: 'xmlpull'
    }```
中的```exclude group: 'xmlpull', module: 'xmlpull'``` 表示什么