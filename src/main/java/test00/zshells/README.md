### 关于bash脚本的四种执行模式的说明

	登陆机器后的第一个shell、通过ssh登陆到远程主机
	配置文件的加载顺序：
	/etc/profile（一定加载）
	~/.bashrc（.bash_profile文件存在则加载）
	~/.bash_profile（以下三个文件按顺序加载，一旦找到并加载其中一个便不再接着加载）
	~/.bash_login
	~/.profile
		
	新启动一个shell进程如运行bash、远程执行脚本如ssh user@remote script.sh
	配置文件的加载顺序：
	~/.bashrc
	 
	执行脚本如bash script.sh、运行头部有如 #!/bin/bash 或 #!/usr/bin/env bash的可执行文件
	配置文件的加载顺序：
	寻找环境变量BASH_ENV，将变量的值作为文件名进行查找，如果找到便加载它
	     	
	[小结]
	通常如果需要远程执行某些开启或关闭进程的动作需要加载环境变量或其他操作时，可以在
	.bashrc文件中做加载或其他操作。如ssh远程执行的命令需要环境变量的支持，则就可以
	在远程主机对应用户的.bashrc做加载/etc/profile的动作
	[注意]
	不能在 ~/.bashrc 文件中有任何输出动作，否则可能将影响bash脚本的执行！
	
	
### 关于ssh内网穿透与内网转发技术
	
	{略}


### 安装和配置

	a) 每个es节点安装expect工具
	$ sudo apt-get install tcl tk expect
	
	b) 设置zshell的环境变量并使其生效
	$ vim /etc/profile
	$ source /etc/profile
	
	
### 使用
	
	# 调用普通脚本命令
	$ zcall `which jps`
	
	# 分发文件或文件夹到每个节点
	$ zcopy xxx/xx ~/xxx
	
	# 登录每个节点执行脚本命令
	$ zcall_by_expect "elasticsearch -d"
	$ zcall_by_expect "ps -ef | grep Elasticsearch | grep -v grep | awk '{print "'$2'"}' | xargs kill -9"
	
	# 启动和关闭es集群
	$ es-cluster start
	$ es-cluster stop
	
	
	