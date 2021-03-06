# InfectStatistic-221701104
疫情统计

描述你的项目，包括如何运行、功能简介、作业链接、博客链接等

### 项目简介

简单的统计程序，通过list命令统计指定文件内的日志内容。

### 如何运行

直接调用主类

```
$ java InfectStatistic -list
```

### 功能

执行`-list`命令时有以下四种参数可供选择：

- `-log` 指定日志目录的位置，该项**必会附带**，请直接使用传入的路径，而不是自己设置路径
- `-out` 指定输出文件路径和文件名，该项**必会附带**，请直接使用传入的路径，而不是自己设置路径
- `-date` 指定日期，不设置则默认为所提供日志最新的一天。你需要确保你处理了指定日期之前的所有log文件
- `-type` 可选择[ip： infection patients 感染患者，sp： suspected patients 疑似患者，cure：治愈 ，dead：死亡患者]，使用缩写选择，如 `-type ip` 表示只列出感染患者的情况，`-type sp cure`则会按顺序【sp, cure】列出疑似患者和治愈患者的情况，不指定该项默认会列出所有情况。
- `-province` 指定列出的省，如`-province 福建`，则只列出福建，`-province 全国 浙江`则只会列出全国、浙江

例如

```
$ java InfectStatistic list -date 2020-01-22 -log D:/log/ -out D:/output.txt
```

会读取D:/log/下的所有日志，然后处理日志和命令，在D盘下生成ouput.txt文件列出2020-01-22全国和所有省的情况（全国总是排第一个，别的省按拼音先后排序）

```
全国 感染患者22人 疑似患者25人 治愈10人 死亡2人
福建 感染患者2人 疑似患者5人 治愈0人 死亡0人
浙江 感染患者3人 疑似患者5人 治愈2人 死亡1人
// 该文档并非真实数据，仅供测试使用
```

### 作业链接
[我的作业地址](https://www.cnblogs.com/pcysoushu/p/12324458.html)
### 博客链接

[我的博客地址](https://www.cnblogs.com/pcysoushu/)