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
	
	
	