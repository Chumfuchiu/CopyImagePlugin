快速集成一个Gradle Plugin模板：
1. 克隆仓库：https://github.com/Chumfuchiu/MicroPlugin.git
2. 在settings.gradle中添加：include ':MicroPlugin'
3. 调整插件发布路径 即调整MicroPlugin/build.gradle中的groupId或者artifactId即可
4. 调整插件ID,即调整template.properties的文件名即可
5. 执行task uploadArchives发布插件到本地

在项目中快速集成发布的插件：
1.添加本地maven地址：
```
        maven {
            url uri(rootDir.toURI().toString() + 'localMaven/repos')
        }
```
2.添加Classpath（调整为你的groupId：artifactId：version）
```
    classpath "com.chumfuchiu.code.plugin:template:1.0"
```
3.在项目中应用插件id即可
```
    apply plugin: 'template'
```
