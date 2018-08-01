
# ELK 
	
	The document about elasticsearch is based on version 6.3.2, please confirm your ES version is right before test.

## 简介

	* ELK 是 Elasticsearch 、 Kibana 和 Logstash 这三个软件集合的简称
	* Elasticsearch 是一个实时分布式搜索和分析引擎
	* Kibana 则为 Elasticsearch 提供了强大的可视化界面
	* Logstash 为用户提供数据采集、转换、优化和输出的能力

## 部署ELK服务

	1.基本设置
		name	
		uuid
		description
		version
		payment_type
		
	2.ES节点设置
	3.kibana节点设置
	4.Logstash节点设置
		vcpu  1,2,4,8,12,16
		mem_size	2G,4G,6G,8G,12G,16G,24G,32G,40G,48G,60G
		node_num  5,15,25,50,100,200,500,...
		host_type  common,ecs-small,ecs-medium,ecs-large
		disk  10G,20G,50G,100G,200G,500G,1000G
	
	5.网络设置
		private_vxnet  私网线路
		
	6.服务环境参数设置
		getway.recover_after_time  		如果未达到期望的节点数，recovery过程将在配置的时间后开始recover操作，默认为5m（5分钟）
		http.cors.allow-origin  			语序跨域资源共享的域，默认为*
		indices.queries.cache.size		接受百分比值或精确值(如512mb)，默认为10%，表示控制缓存的内存大小
		indices.memory.index_buffer_size	接受百分比值或精确值(如512mb)，默认为10%，表示总堆内存的10%将被用于索引缓存被所有的分片共享
		indices.requests.cache.size		分片级的请求缓存对每一个分片做本地缓存，这个缓存在节点级别进行管理，默认为堆内存的1%
		input_conf_content
		
		...
	
	7.用户协议
		阅读并同意用户协议之后即可开始部署应用。
		
	
	
# Elasticsearch
	
	ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。
	Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算
	中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。
	
## ES基本概念

	Index
		类似于mysql数据库中的database。
	Type	
		类似于mysql数据库中的table表，es中可以在index中建立type(table)，通过mapping进行映射。
		ES6.0以后，每个索引只能创建一个类型（否则可能会有mapping would have more than 1 type: [type1xx, type2xx]" 的error）
	Document
		ES存储的数据时文档型的，一条数据对应一篇文档即相当于mysql数据库中的一行记录，一个文档可以有多个字段也就是mysql数据库一行可以有多列。
	Field
		一个文档中对应的多个列，与mysql数据库中的每一列对应。
		
	mapping
		可以理解为mysql或者solr中对应的schema，只不过有些时候ES中的mapping增加了动态识别功能，感觉很强大的样子，其实实际生产环境中不建议
		使用，最好还是开始制定好了对应的schema为主。
		映射定义了type中的每个字段的数据类型以及这些字段如何分词等相关特性。mapping下的dynamic属性用于配置检测新发现的字段，主要有三个值：
		true:新发现的字段添加到映射中。（默认）
		flase:新检测的字段被忽略。必须显式添加新字段。
		strict:如果检测到新字段，就会引发异常并拒绝文档
	indexed
		就是名义上的建立索引。mysql一般会对经常使用的列增加相应的索引以提高查询速度，”而在es中默认都是会加上索引的“，除非你特殊制定不建立索
		引只是进行存储用于展示，这个需求看你具体的需求和业务进行设定了。
	QueryDSL
		类似于mysql的sql语句，只不过在es中使用的是json格式的查询语句。
	RestfulAPI
		GET[查]/POST[增改]/PUT[改]/DELETE[删]，类似于sql中的select/create/update/delete
		
		[注1] REST(Representational State Transfer，即表述性状态传递)
		这是一种软件架构的设计风格而不是标准，只是提供了一组设计原则和约束条件，它主要用于客户端和服务器交互类的软件设计。基于这个风格设计的
		软件可以更简洁更有层次，更易于实现缓存等机制。在目前主流的三种WEB服务交互方案中，REST相比于SOAP(简单对象访问协议)以及XML-RPC更加
		简单明了。它使用典型的HTTP方法，诸如GET/PUT/POST/DELETE来实现资源的增删改查等操作，即通过HTTP动词来实现资源的状态扭转。
		
		[注2] CURL的方式访问HTTP协议或其他协议的资源示例：
		$ curl [-u userxx:passxx] [-I] [-X GET|POST|PUT|DELETE] [-H 'Content-Type:application/json'] [-d 'BODY_DATA']
		  http://nimbusz:9200/xxx?q=xxx
		$ curl -O ftp://hostxxx:8080/xxx.txt 下载
		$ curl -v http://www.baidu.com  显示一次HTTP请求的通信过程
		
## 倒排索引和分词器
	
	倒排索引：
		ES使用一种称为倒排索引的结构，适用于快速的全文检索。一个倒排索引由文档中所有不重复词的列表组成，对于每一个词，对应一个包含它的文档列表。
		
		文档编号			文档内容
		------------------------------------------------------------------------
		1		谷歌地图之父跳槽Facebook
		2		谷歌地图之父加盟Facebook
		3		谷歌地图创始人拉斯离开谷歌加盟Facebook
		4		谷歌地图之父跳槽Facebook与Wave项目取消有关
		5		谷歌地图之父拉斯加盟社交网站Facebook
		
		由于中文和英文等语言不同，单词之间没有明显的分割符号，所以首先要用分词系统将文档自动切分成单词序列。这样每个文档转换为由单词序列构成的
		数据流，为了系统的后续处理方便，需要对每个不同的单词赋予一个唯一的单词编号，同时记录下哪些文档包含这个单词、出现单词的位置、单词的数量。
		在如此处理结束后我们就可以得到简单的倒排索引：
		
		单词ID	单词		倒排列表(文档编号 单词位置 词频TF)
		------------------------------------------------------------------------
		1		谷歌		(1,<0>,1),(2,<0>,1),(3,<0,11>,2),(4,<0>,1),(5,<0>,1)
		2		地图		(1,<2>,1),(2,<2>,1),(3,<2>,1),(4,<2>,1),(5,<2>,1)
		3		之父		(1,<4>,1),(2,<4>,1),(4,<4>,1),(5,<4>,1)
		4		跳槽		(1,<6>,1),(4,<6>,1)
		5		Facebook	(1,1),(2,1),(3,1),(4,1),(5,1)
		6		加盟		(2,1),(3,1),(5,1)
		7		创始人	(3,1)
		8		拉斯		(3,1),(5,1)
		9		离开		(3,1)
		10		与		(4,1)
		11		Wave		(4,1)
		12		项目		(4,1)
		13		取消		(4,1)
		14		有关		(4,1)
		15		社交		(5,1)
		16		网站		(5,1)
		
		上面这样直接分词的存在的问题：
		a) 不应该区分大小写（如 Quick、quick）
		b) 不应该区分相同的词根（如 fox、foxes；dog、dogs）
		c) 相近的次应该有关联（如 jumped、leap）
		因此，在建立倒排索引的时候，会使用所谓的 标准化规则（normalization）对拆分出的各个单词进行相应的处理，以提升搜索的效率。	
	
	分词器：
		从一串文本中切分出一个个的词条，并对每个词条进行标准化，包括三个部分，即：
		a) character filter：预处理，过滤掉HTML标签、特殊符号转换等
		b) tokenizer：分词器
		c) token filter：标准化
		内置的分词器如下：
		standard分词器：（默认的）它会将单词转化成小写的形式，并去除停用词和标点符号，支持中文采用的方法为单字切分
		simple分词器：首先通过非字母字符来分割文本信息，然后将词汇单元统一为小写形式，该分词器会去除掉数字类型的字符
		whitespace分析器：仅仅是取出空格，对字符没有小写化，也不支持中文，并且不对词汇单元进行其他的标准化处理
		language分词器：特定语言的分词器，不支持中文
		
## 安装ES
	安装步骤：（略）
	安装中文分词器：
	a) git clone https://github.com/medcl/elasticsearch-analysis-ik.git
	b) cd elasticsearch-analysis-ik & mvn clean install -Dmaven.test.skip=true
	c) 在es的plugins文件夹下创建目录ik
	d) 将编译后生成的elasticsearch-analysis-ik-{version}.zip 移动到ik目录下，解压会生成elasticsearch文件夹（解压完成后zip包可删除）
	e) 将解压生成的elasticsearch文件夹下的所有内容再拷贝到ik目录下（es默认去 plugins/ik 目录下加载分词器信息）
	f) 验证是否安装成功，在es启动的时候，日志信息会显示 ...loaded plugin [analysis-ik]...
	
	ES安装可能出现的问题：
	a) can not run elasticsearch as root
		解决方式1：bin/elasticsearch -Des.insecure.allow.root=true
		解决方式2：修改bin/elasticsearch文件，增加 ES_JAVA_OPTS="-Des.insecure.allow.root=true" 属性
		解决方式3：使用新建的用户启动es程序（推荐）
	
	b) 再次启动显示已杀死|Cannotallocate memory，需要调整JVM的内存大小：
		解决方式：修改bin/elasticsearch文件，增加 ES_JAVA_OPTS="-Xms512 -Xmx512" 属性
		        -Xms分配堆最小内存，默认为物理内存的1/64；-Xmx分配最大内存，默认为物理内存的1/4。
		        -XX:PermSize分配非堆最小内存，默认为物理内存的1/64；-XX:MaxPermSize分配最大内存，默认为物理内存的1/4
			   合理的内存分配是程序正常稳定的运行的基础。不然内存溢出可就麻烦了。
	
	c) low disk watermark [85%] exceeded on [xxx]
		解决方式：清理磁盘空间
	
	
	d) max file descriptors [4096] for elasticsearchprocess is too low, increase to at least [65536]
		解决方式：maxfile descriptors为最大文件描述符，设置其大于65536即可（*表示所有用户，如果是特定用户最好配置成特定的用户）
		$ sudo vim /etc/security/limits.conf
			* soft nofile 65536
			* hard nofile 131072
			* soft nproc 4096
			* hard nproc 4096
	
	e) max number of threads [1024] for user [elsearch] likely too low, increase to at least [4096]
		解决方式：
		$ sudo vim /etc/security/limits.d/90-nproc.conf
			esxxx soft nproc 4096
			root soft nproc unlimited
		
	f) max virtual memory areas vm.max_map_count [65530]is too low, increase to at least [262144]
		解决方式：max_map_count文件包含限制一个进程可以拥有的VMA(虚拟内存区域)的数量，系统默认是65530，修改成262144。
		$ sudo vim /etc/sysctl.conf
			vm.max_map_count=262144
		$ sudo sysctl -p (使修改生效)
		
	g) 实现远程连接ES服务：
		解决方式：修改配置文件 config/elasticsearch.yml，配置 network.host: 192.168.1.106
		如果再次启动出现 bind exception，则说明 network.host 配置错误！
		
## 安装 ES HEAD 插件		   
	
	elasticsearch-head是ES的集群管理工具，可以用于数据的浏览和查询。它托管在github上面。运行head插件会用到grunt，而grunt需要npm包
	管理器。所以使用head插件也需要安装 nodejs。注意 ES-5.0之后 elasticsearch-head 插件不在作为插件放在 plugins目录下了，如果需要
	我们可以手动从github下载安装本地。
	
	安装步骤：
	a) 编辑elasticsearch.yml，配置es允许跨域访问：
	   http.cors.enabled: true
	   http.cors.allow-origin: "*"
	b) 修改 elasticsearch-head-master/Gruntfile.js
	   在connection->server->options下面添加：hostname:'*'，允许所有ip可以访问
	c) 修改 head插件的默认连接地址
	   this.base_uri = this.config.base_uri || this.prefs.get("app-base_uri") || "http://nimbusz:9200";
	d) 下载安装 nodejs（https://nodejs.org/en/download/ ），并配置环境变量 $NODEJS_HOME；
	e) npm install -g grunt-cli （sudo -i进入到root用户，使用root用户安装grunt，grunt是javascript项目的构建工具，grunt -version） 
	f) cd elasticsearch-head-master & npm install  （安装phantomjs的过程需要一定的时间）
	g) 启动head客户端程序：cd elasticsearch-head-master & nohup grunt server > /dev/null 2>&1 &
	
## 安装 kibana 客户端工具

	kibana也是针对ES的开源分析及可视化平台，kibana除了head插件的功能，还能够执行高级的数据分析，并能以图表、表格、地图的形式查看数据。
	
	安装步骤：
	a) 下载、解压
	b) 编辑配置文件

## mapping
	
	映射定义了type中的每个字段的数据类型以及这些字段如何分词等相关特性。
	mapping下的dynamic属性用于配置检测新发现的字段，主要有三个值：
	true:新发现的字段添加到映射中。（默认）
	flase:新检测的字段被忽略。必须显式添加新字段。
	strict:如果检测到新字段，就会引发异常并拒绝文档
	
	
	mapping支持的数据类型：
	字符型：string，string类型主要包括以下两种类型
		  text: 用来索引长文本，在建立索引前会将这些文本“转化为小写进行分词”，转化为词的组合，建立索引。允许es来检测这些词语。text类型不能用来排序和聚合。
		  keyword: 不会被分词，可以被用来检测过滤、排序、聚合。keyword类型子弹只能用本身来进行检索。
	数字型：byte、short、integer、long（默认）、double（默认）、float
	日期型：date（"1990-09-09"）
	布尔型：boolean
	二进制型：binary
	对象类型：_object_ 用于单个json对象
	嵌套类型：_nested_ 用于json数组
	数组类型：不需要专门指定数组类型的type，例如：
		字符型数组：["one","two"]
		整形数组：[1,2,3]
		数组型数组：[1,[2,3]] 等价于 [1,2,3]
		对象数组：[{"name":"zhangsan", "age":23}, {"name":"lisi", "age":24}]
	地理位置类型：主要包括以下两种类型
		  地理坐标类型：_geo_point_ 用于经纬度坐标
		  地理形状坐标：_geo_shape_ 用于蕾丝布多边形的复杂形状
	特定类型：主要包含以下几种类型
		IPV4类型：_ip_ 用于ipv4地址
		Completion类型：_completion_ 提供自动补全建议
		Token Count类型：_token_count_ 用于统计做了标记的字段的index数目，该值会一直增加，不会因为过滤条件而减少
	
	[注意] 
	字段的类型一旦确定，就不能修改了。要想修改字段的类型，只能新建一个新的索引，把旧索引中的数据再导入到新的索引中。那么如何保证在不重启客户端应用
	程序的条件下，实现索引的重建呢？可以使用别名关联的方式实现。
	i) 首先将旧的索引关联别名（例如 my_alias_index）；
	ii) 然后使用scroll查询和bulk创建的方式将旧索引的数据导入到新建索引中；
	iii) 最后将旧索引的别名关联取消，以及将新索引关联到别名 my_alias_index 上。这样客户端还是使用别名my_alias_index访问索引库，不必重启客户端。
	
	a) 为索引 lib 起一个别名 lib_myalias
	PUT lib/_alias/lib_myalias
	
	b) 取消索引lib对别名lib_myalias的关联，然后将索引lib2关联到别名lib_myalias上
	POST /_aliases
	{
	  "actions":[
	    {"remove":{"index":"lib", "alias":"lib_myalias"}},
	    {"add":{"index":"lib2", "alias":"lib_myalias"}}
	  ]
	}
	
	
	mapping支持的主要属性：
	dynamic：（true|false|strict）用于配置检测新发现字段的策略，默认为true。
	store：（true|false）用于指定是否将原始字段写入索引。在Lucene中，高亮功能和store属性是否存储息息相关，因为需要根据偏移位置
	       到原始文档中找到关键字才能加上高亮的片段。在Elasticsearch，因为_source中已经存储了一份原始文档，可以根据_source中的原始文档实现
	       高亮，在索引中再存储原始文档就多余了，所以Elasticsearch默认是把store属性设置为no。注意:如果想要对某个字段实现高亮功能，_source
	       和store至少保留一个。
	_source：{"enabled":false} 设置是否保存_source中包含的那些字段等信息。_source字段默认是存储的， 什么情况下不用保留_source字段？如果某
	       个字段内容非常多，业务里面只需要能对该字段进行搜索，最后返回文档id，查看文档内容会再次到mysql或者hbase中取数据，把大字段的内容存在ES
	       中只会增大索引，这一点文档数量越大结果越明显，如果一条文档节省几KB，放大到亿万级的量结果也是非常可观的。
	       如果只想存储某几个字段的原始值到ES，可以设置_source:{"enabled":false}，并单独设置每个需要存储的字段的store属性为true。
	index：（true|false）设置是否分词，从而生成倒排索引。设置成false，字段将不会被索引（当使用没被索引的字段进行查询时会抛出异常）。
	       默认值为true，会将字符串、数字、日期、布尔等类型的值 或分词[针对text]或不分词[针对非text]处理之后 放到倒排索引中。
	_all：{"enabled":false} 设置_all字段是否可用等信息。_all及相关的include_in_all在 ES6.0 之后已经废弃。
	       可使用其替代属性copy_to进行设置。
	copy_to：设置每个字段的值是否加入到 copy_to 设置的字段中。
	       功能类似于ES5.x及之前的版本的_all。
	fields：{"raw":{"type":"keyword", "index":true}}，可以对一个字段提供多种索引模式，如一个字段的值一个分词，一个不分词。
	fielddata：{"format":"disabled"}，针对分词字段，参与排序或聚合时提高性能，不分词字段统一建议使用doc_value。
	doc_values：（true|false）只能用于不分词的字段，设置成false能对排序和聚合性能有较大提升，节约内存。默认都开启true。
	analyzer：（"ik"）指定分词器，默认为 standard analyzer。
	search_analyzer：设置索引时的分词器，默认跟analyzer是一致的。
	ignore_above：256，超过256个字符的文本将会被忽略，不被索引。默认为256。
	boost：（1.23）字段级别的分数加权，默认为1.0。
	date_detection：设置是否按照默认的格式（yyyy-MM-dd）识别侦测date类型。
	index_options："docs"，有四个可选参数docs(索引文档号)、freqs(文档号+词频)、positions(文档号+词频+位置，通常用来距离查询)、
	               offsets(文档号+词频+位置+偏移量，通常用来高亮字段)。分词字段默认为positions，其他的默认为docs。
	norms：{"enable":true, "loading":"lazy"}，分词字段的默认配置，不分词字段默认为{"enable":false}，存储长度因子和索引时boost，
	               建议对参与评分的额字段使用，但会额外增加内存的消耗量。
	null_value："NULL"，设置一些确实化字段的初始值。只有string字段可以使用，分词字段的null值也会被分词。
	position_increment_gap：0，影响距离查询或近似查询，可以设置在多值字段的数据上或分词字段上，查询时可以指定slop间隔，默认值为100。
	similarity："BM25"，指定一个字段评分策略，仅仅多字段类型或分词类型有效。默认是TF/IDF算法。
	term_vector：（no|yes|with_positions|with_offsets），默认不存储向量信息。
	dynamic_templates：使用模板定义 mapping。
	
	
	底层存储格式示例：
	{
	  "name":"Tom",
	  "age":23,
       "birthday":"1990-09-09",
       "address":{
         "country":"china",
         "province":"gunagdong",
         "city":"shenzhen"
       }
	}
	==>
	{
       "name":["Tom"],
       "age":[23],
       "birthday":["1990-09-09"],
       "address.country":["china"],
       "address.province":["gunagdong"],
       "address.city":["shenzhen"]
	}
	
	{
      "persons":[
        {"name":"zhangsan", "age":23},
        {"name":"lisi", "age":24}
      }
	}
	==>
	{
       "persons.name":["zhangsan","lisi"],
       "persons.age":[23,24]
	}
	
	创建索引时指定mapping：
	PUT lib6
	{
	  "settings": {
	    "number_of_shards": 5,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "books":{
	      "properties": {
	        "title":{"type": "text"},
	        "name":{"type": "text", "analyzer": "standard"},
	        "publish_date":{"type": "date", "format":"yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis", "index":false},
	        "price":{"type": "double"},
	        "number":{"type": "integer"}
	      }
	    }
	  }
	}
	PUT lib7 （创建索引lib7，不保存原始文档到索引，但会单独保存原始文档的interests字段到索引）
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "user":{
	      "_source": {
	        "enabled": false
	      },
	      "properties": {
	        "name":{
	          "type":"keyword"
	        },
	        "interests":{
	          "type": "text",
	          "store": true
	        }
	      }
	    }
	  }
	}
	PUT lib8 （创建索引lib8，并将一些字段的值加入到copy_to字段中）
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "emp":{
	      "properties": {
	        "first_name":{
	          "type": "text",
	          "copy_to": "full_name"
	        },
	        "last_name":{
	          "type": "text",
	          "copy_to": "full_name"
	        },
	        "age":{
	          "type": "integer"
	        }
	      }
	    }
	  }
	}
	PUT lib19 （创建索引lib9，不自动侦查日期字符串（侦查格式默认为yyyy-MM-dd），而是手动指定日期格式为 yyyy-MM-dd HH:mm:ss 或 epoch_millis）
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "user":{
	      "properties": {
	        "name":{
	          "type":"keyword"
	        },
	        "interests":{
	          "type": "text"
	        },
	        "birthday":{
	          "type": "date",
	          "format": "yyyy-MM-dd HH:mm:ss||epoch_millis"
	        }
	      },
	      "date_detection": false
	    }
	  }
	}
	

## 在kibana中对ES进行基本的增删改查

	// 添加索引
	PUT /lib/ (默认为5个分片和1个副本)
	PUT /lib/ 
	{
	  "settings":{
	    "index":{
	      "number_of_shards": 5,
	      "number_of_replicas": 0
	    }
	  }
	}
	
	// 查看索引的配置
	GET _all/_settings
	GET lib/_settings
	
	// 添加一条记录(指定插入记录的ID用PUT，不指定用POST)
	PUT /lib/user/1
	{
		"first_name": "Jane",
		"last_name": "Smith",
		"age": 32,
		"about": "I like to collect rock albums",
		"intersts": ["music"]
	}
	POST /lib/user
	{
		"first_name": "Douglas",
		"last_name": "Fir",
		"age": 23,
		"about": "I like to build cabinets",
		"intersts": ["forestry"]
	}
	
	// 查询记录
	GET /lib/user/_mapping
	GET /lib/user/1
	GET /lib/user/_search
	GET /lib/user/1?_source=first_name,last_name
	GET /lib/user/_search?q=first_name:Smith
	GET /lib/user/_search?q=intersts:music&sort=age:desc
	GET /lib/user/_search?q=haidian,football （不指定q查询的字段，则意为查询某个字段中包含haidian或football的文档，性能很低）
	GET /_mget
	{
	  "docs": [
	    {
	     "_index":"lib",
	     "_type":"user",
	     "_id":1
	    },
	    {
	     "_index":"lib",
	     "_type":"user",
	     "_id":2
	    },
	    {
	     "_index":"lib2",
	     "_type":"user2",
	     "_id":1
	    }
	  ]
	}
	GET /lib/user/_mget
	{
	  "docs": [
	    {
	    	"_type": "user",
	     "_id":1
	    },
	    {
	     "_id":2
	    },
	    {
	     "_id":1
	    }
	  ]
	}
	GET /lib/user/_mget
	{
	  "ids":[1,2,3]
	}
	
	// 多index查询
	GET _search
	GET /lib3,lib4/_search
	GET /*3,*4/_search
	GET _all/_search
	GET _all/user,iterms/_search
	
	// 更新记录（包括覆盖更新和局部字段更新）
	PUT和POST方式更新文档的不同点
	---------------------------------------------------------------------------------
     old_doc[标记为deleted] ---> new_doc             old_doc[标记为deleted] ---> new_doc
                 /| |      3    |                                 /|       2    |
                 1| |2          |4                                1|            |3
                  | |/          |/                                 |            |/ 
	             客户端操作或展示程序                                客户端操作或展示程序
	---------------------------------------------------------------------------------             
	1) post方式更新数据比put方式网络传输的次数要少（post方式从查询文档到修改文档再到创建新的文档都是在es内部实现的），从而提高了性能。   
	2) put方式先要从es中查询数据展示给客户端，客户端提交修改到es集群进行更新，由于这段时间很长，这就大大增加了并发的冲突性。而post方式并发冲突的可能性较低。
	3) 对于客户端来说，put方式只能整个覆盖更新原数据，而post方式可以使用doc进行局部更新原数据。
	         
	PUT /lib/user/1
	{
		"first_name": "Jane",
		"last_name": "Smith",
		"age": 24,
		"about": "I like to collect rock albums",
		"intersts": ["music"]
	}
	POST /lib/user/1/_update
	{
		"doc":{
			"age": 24
		}
	}
	
	// 删除文档和索引（实际是先将文档标记为deleted，ES在适当的时间会将标记为deleted的文档真正的删除）
	DELETE /lib/user/1
	DELETE /lib2
	
## 使用 BULK API 实现批量操作
	
	bulk会把要处理的数据加载到内存中，所以一次处理的数据量是有限制的，最佳的数据量不是一个确定的数值，它取决于你的硬件及数据操作的复杂性。
	一般建议是1000-5000个文档，大小建议是在5-15M，默认不能超过100M，可以通过 config/elasticsearch.yml设置。
	
	四种操作：
	create：文档不存在时创建，如果数据存在则使用create会操作失败
	index：创建新文档或替换已有的文档
	update：更新文档
	delete：删除一个文档
	
	示例：
	POST /lib2/books/_bulk（如果中间存在失败的操作，则返回结果中包含 error:true（其他操作是成功的））
	{"index":{}}
	{"title":"CSS", "price":12}
	{"index":{"_id":1}}
	{"title":"Java", "price":55}
	{"index":{"_index":"lib2", "_type":"books"}}
	{"title":"Html5", "price":45}
	{"index":{"_index":"lib2", "_type":"books", "_id":3}}
	{"title":"PHP", "price":35}
	{"index":{"_id":4}}
	{"title":"Python", "price":50}
	{"create":{"_index":"lib2", "_type":"books", "_id":5}}
	{"title":"JS", "price":51}
	{"delete":{"_index":"lib2", "_type":"books", "_id":5}}
	{"update":{"_index":"lib2", "_type":"books", "_id":4}}
	{"doc":{"price":48}}
	
	bulk批量操作的json格式被设计成如上所述的格式：
	{action:{metadata}}\n
	{requestbody}\n
	那么为什么不适用如下的格式呢？
	[
	  {
	    "action": {...}
	    "data": {...}
	  },
	  ...
	]
	因为这种格式可读性好，但是内部处理可就麻烦了（耗费内存、增加JVM的开销）：
	1) 首先这种格式需要将json数组解析为jsonArray对象，在内存中需要有一份json文本的拷贝，另外还要有一个jsonArray对象。
	2) 然后解析json数组里面的每一个json，对每个请求中的document进行路由。
	3) 然后为路由到同一个shard上的多个请求，创建一个请求数组，并将这个请求数组序列化，发送到对应的节点上去。
	而使用ES bulk的这种格式
	1) 不用将其转化为json对象，直接按照换行符切割json，内存中不需要json文本的拷贝；
	2) 对每两个一组的json，读取meta，直接进行文档的路由，将对应的json发送到对应的节点上去即可。
	
## 使用脚本的方式操作数据
	
	POST /lib3/user/1/_update （将_id为1的用户年龄增加1，支持POST和GET）
	{
	  "script":"ctx._source.age+=1"
	}
	POST lib3/user/1/_update （为_id为1的用户的兴趣数组添加元素 football）
	{
	  "script": {
	    "source": "ctx._source.interests.add(params.myinterest)",
	    "params": {
	      "myinterest": "football"
	    }
	  }
	}
	POST lib3/user/1/_update （删除_id为1的用户的那些为null的兴趣元素，注意脚本更新操作的时候一定要带_id值）
	{
	  "script": {
	    "source": "ctx._source.interests.remove(ctx._source.interests.indexOf(params.myinterest))",
	    "params": {
	      "myinterest": null
	    }
	  }
	}
	POST lib3/user/2/_update （如果_id=2用户的年龄为25，则删除该用户，否则什么都不做）
	{
	  "script": {
	    "source":"ctx.op = ctx._source.age==params.p_age ? 'delete' : 'none'",
	    "params": {
	      "p_age":25
	    }
	  }
	}
	POST lib3/user/9/_update （如果_id为9的用户存在则将其年龄增加1，不存在则插入定义的文档）
	{
	  "script": {
	    "source":"ctx._source.age+=1"
	  },
	  "upsert": {
	    "name": "lisi",
	    "age": 25,
	    "address": "beijing"
	  }
	}
	
## ES的数据版本控制

	ES采用了乐观锁的机制来保证数据的一致性，也就是当用户对document操作的时候，并不需要对document进行加锁和解锁的操作，只需要指定要操作的
	版本即可。当版本号一致的时，ES允许该操作顺利执行，当版本号存在冲突的时候，ES会显示冲突并抛出 VersionConflictEngineException异常。
	ES版本号的取值范围为 2^63-1。
	内部版本控制：使用的是_version。
	外部版本控制：与内部版本控制不同的是外部版本控制不再是检查_version是否与请求中指定的数值相同，而是检查当前的_version是否比指定的数值
	小。如果请求成功，那么外部的版本号就会被存储到文档的_version中。为了保持_version与外部版本控制的数据一致，使用version_type=external
	
	内部版本控制示例：（通常用于一般ES文档的版本控制）
	PUT /lib/user/6 （正常返回结果 _version:1）
	{
		"name":"zhangsan",
		"age":23
	}
	PUT /lib/user/6?version=2 (覆盖修改不能成功，版本号不相符)
	{
		"name":"zhangsan",
		"age":24
	}
	POST /lib/user/6/_update?version=1 （局部字段修改成功，版本号相符，版本号自动+1变为2）
	{
		"doc":{"age":25}
	}
	POST /lib/user/6/_update?retry_on_conflict=3 （当版本冲突的时候重试3次，这个参数同样可以用于外部版本控制） 
	{
		"doc":{"age":25}
	}
	
	外部版本控制示例：（通常用于外部数据库导入的数据，版本参数的值一般可以设置为时间戳）
	PUT /lib/user/7 （正常返回结果 _version:1）
	{
		"name":"zhangsan",
		"age":23
	}
	PUT /lib/user/6?version=1&version_type=external （局部字段修改不能成功，请求版本号不大于当前版本号）
	{
		"name":"zhangsan",
		"age":24
	}
	
## 基本的query查询
	
	数据准备：
	PUT lib3
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "user":{
	      "properties": {
	        "name":{"type": "text"},
	        "age":{"type": "integer"},
	        "address":{"type": "text"},
	        "interests":{"type": "text"},
	        "birthday":{"type": "date"}
	      }
	    }
	  }
	}
	PUT lib3/user/_bulk
	{"index":{"_id":1}}
	{"name":"zhangsan", "age":23, "address":"beijing", "interests":"i like music pingpengqiu painting", "birthday":"1990-05-05"}
	{"index":{"_id":2}}
	{"name":"lisi", "age":25, "address":"beijing", "interests":"i like physics mathematics running", "birthday":"1990-06-06"}
	{"index":{"_id":3}}
	{"name":"wangwu", "age":28, "address":"shanghai", "interests":"i like sleeping music pingpengqiu", "birthday":"1990-06-09"}
	{"index":{"_id":4}}
	{"name":"xiaojuan", "age":23, "address":"beijing", "interests":"i like music and playing piano", "birthday":"1990-05-05"}
	
	
	a) term查询和terms查询：
	term query 会去倒排索引中寻找确切的term，它并”不知道分词器的存在“。这种查询适合keyword、numberic、date。
	term表示查询某个字段里含有某个关键词，而terms表示查询某个字段里含有多个关键词的意思。
	
	GET /lib3/user/_search (查询名字中包含zhangsan的文档)
	{
	  "query":{
	    "term":{"name":"zhangsan"}
	  }
	}
	GET /lib3/user/_search（查询兴趣中包含music或pingpengqiu的文档，显示version字段，返回从又开始的两条记录）
	{
	  "version":true,
	  "from":0,
	  "size":2,
	  "query":{
	    "terms":{
	      "interests":["music", "pingpengqiu"]
	    }
	  }
	}
	
	b) match查询
	match “知道分词器的存在”，会对field进行分词操作，然后再查询。
	match_all 表示查询所有符合条件的文档。
	multi_match 与 match 相似，不过他可以指定多个字段。
	match_phrase 表示短语匹配查询，当查询的短语与原文档中包含的短语一致时才能匹配到。
	match_phrase_prefix 表示前缀匹配，当查询的前缀与原文档中一致即可匹配到。
	
	GET /lib3/user/_search （首先对zhangsan lisi进行分词，然后查找，得到zhangsan和lisi的记录）
	{
	  "query": {
	    "match": {
	      "name": "zhangsan lisi"
	    }
	  }
	}
	GET /lib3/user/_search (查询所有记录)
	{
	  "query": {
	    "match_all":{}
	  }
	}
	GET /lib3/user/_search （查询name和interests字段中含有zhangsan或sleeping的记录）
	{
	  "_source": {
	    "includes":["name", "inter*"],
	    "excludes":["age", "addr*", "birth*"]
	  },
	  "query": {
	    "multi_match":{
	      "fields": ["name", "interests"],
	      "query": "zhangsan sleeping"
	    }
	  }
	}
	GET /lib3/user/_search（查询短语完全匹配短语music and playing piano的文档，返回的文档只包含name和interests两个字段，排序不能用在分词字段上）
	{
	  "_source":["name", "interests"],
	  "query": {
	    "match_phrase": {
	      "interests": "music and playing piano"
	    }
	  },
	  "sort":[
		{ "age":{"order":"desc"} }
		{ "birthday":{"order":"desc"s} }
	  ]
	}
	GET /lib3/user/_search （查询地址的前缀是 beij 的文档，默认不区分大小写）
	{
	  "query":{
	    "match_phrase_prefix": {
	      "address": "beij"
	    }
	  }
	}
	
	c) range 范围查询：
	
	GET /lib3/user/_search
	{
	  "query":{
	    "range": {
	      "birthday": {
	        "from": "1990-05-05",
	        "to": "1990-06-01",
	        "include_lower":true,
	        "include_upper":false
	      }
	    }
	  }
	}
	GET /lib3/user/_search
	{
	  "query":{
	    "range": {
	      "age": {
	        "gte": 20,
	        "lte": 23
	      }
	    }
	  }
	}
	
	d) wildcard查询
	允许使用通配符 * 和 ? 进行查询。
	
	GET /lib3/user/_search
	{
	  "query": {
	    "wildcard": {
	      "name": "zhang*"
	    }
	  }
	}
	
	e) fuzzy模糊查询
	value：查询的关键字。
	boost：查询的全职，默认为1.0。
	min_similarity：设置匹配的最小相似度，默认为0.5，对于字符串，取值为0-1任意数；对于数值取值可以大于1；对于日期类型取值为1d、1h、1m等。
	prefix_length：指明区分词项的共同前缀长度，默认是0。
	max_expansions：查询中的词项可以扩展的数目，默认可以无限大。
	
	GET /lib3/user/_search （在interests字段中模糊查询slepig，结果高亮显示）
	{
	  "query": {
	    "fuzzy": {
	      "interests": "slepig"
	    }
	  },
	  "highlight": {
	    "fields": {
	      "interests": {}
	    }    
	  }
	}
	
	f) 使用中文分词器
	ik带有两个分词器，分别是 ik_max_word和 ik_smart
	ik_max_word: 会将文本做最细粒度的拆分，尽可能多的拆分出词语。
	ik_smart: 会做最粗粒度的拆分，已被分出的词语将不会被其他词语所占有。
	
	PUT lib4
	{
	  "settings": {
	    "number_of_shards": 5,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "user":{
	      "properties": {
	        "name": {
	          "type": "text",
	          "analyzer": "ik_max_word"
	        },
	        "address":{
	          "type": "text",
	          "analyzer": "ik_max_word"
	        },
	        "age":{
	          "type": "integer"
	        },
	        "birthday":{
	          "format":"yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis",
	          "type": "date"
	        },
	        "interests":{
	          "type": "text",
	          "analyzer": "ik_max_word"
	        }
	      }
	    }
	  }
	}
	
	GET /lib4/user/_search
	{
	  "query":{
	    "term":{
	      "interests":"喝酒"
	    }
	  }
	}
	
	g) filter 查询
	filter查询是不计算相关性的，同时filter查询及其结果可以一直保存在cache中，因此filter查询的速度要快于query。
	
	GET /lib4/user/_search（term查询年龄为23的记录）
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "term": {
	          "age": 23
	        }
	      }      
	    }
	  }
	}
	GET /lib4/user/_search （terms查询兴趣中包含”喝酒“、”听音乐“两个词的记录）
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "terms": {
	          "interests": [
	            "喝酒",
	            "听音乐"
	          ]
	        }
	      }      
	    }
	  }
	}
	GET /lib4/user/_search （短语前缀匹配名字前缀为”张“的记录）
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "match_phrase_prefix":{
	          "name":"张"
	        }
	      }
	    }
	  }
	}
	GET /lib4/user/_search （范围查询年龄在20-25的记录）
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "range": {
	          "age": {
	            "gte": 20,
	            "lte": 25
	          }
	        }
	      }
	    }
	  },
	  "sort": [
	    {
	      "age": {
	        "order": "desc"
	      }
	    }
	  ]
	}
	GET /lib4/user/_search （通配符查询名字中符合”张?“的记录）
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "wildcard":{
	          "name":"张?"
	        }
	      }
	    }
	  }
	}
	GET /lib4/user/_search （模糊查询兴趣中包含”喝好酒“的记录）
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "fuzzy":{
	          "interests":"喝好酒"
	        }
	      }
	    }
	  }
	}
	GET lib4/user/_search  (查询名字不为空的记录)
	{
	  "query": {
	    "bool": {
	      "filter": {
	        "exists": {
	          "field": "name"
	        }
	      }
	    }
	  }
	}
	
	h) 布尔查询以及复合查询
	should: 相当于连接条件or
	must: 相当于连接条件and
	must_not: 相当于连接条件not
	filter: 必须匹配，但它以不评分、过滤模式来执行。这些语句对评分没有贡献，只是根据过滤条件来排除或包含文档。
	
	相关性得分是如何组合的。每一个子查询都独立地计算文档的相关性得分，一旦他们的的二分被计算出来，bool查询就将这些得分进行合并并返回一个代表整个
	布尔操作的得分。下面的查询将用于查找title字段匹配 how to make millons 并且不被标识为 spam 的文档，那些被标识为 starred 或在 2014 之
	后的文档，将比另外那些文档拥有更高的排名。如果两者都满足，那么它的排名将会更高：

	GET lib6/xxx/_search
	{
	  "query": {
	    "bool": {
	      "must": [
	        {
	          "match": {
	            "title": "how to make millons"
	          }
	        }
	      ],
	      "must_not": [
	        {
	          "match":{
	            "tag":"spam"
	          }
	        }
	      ],
	      "should": [
	        {
	          "match": {
	            "tag": "starred"
	          }
	        },
	        {
	          "range": {
	            "date": {
	              "gte": "2015-01-01"
	            }
	          }
	        }
	      ]
	    }
	  }
	}

	如果没有must语句，那么至少需要能够匹配其中的一条should语句。但是，如果存在至少一条must语句，则对should语句的匹配没有要求。如果我们不想
	因为文档的时间而影响得分，可以使用filter语句来重写前面的例子，通过将range查询移到filter语句中，我们将它转成不评分的查询，将不再影响文档
	的相关性排名。由于它仙现在是一个不评分的查询，可以使用各种对filter查询有效的优化手段来提高性能：

	GET lib6/xxx/_search
	{
	  "query": {
	    "bool": {
	      "must": [
	        {
	          "match": {
	            "title": "how to make millons"
	          }
	        }
	      ],
	      "must_not": [
	        {
	          "match":{
	            "tag":"spam"
	          }
	        }
	      ],
	      "should": [
	        {
	          "match": {
	            "tag": "starred"
	          }
	        }
	      ],
	      "filter": {
	        "range": {
	          "date": {
	            "gte": "2015-01-01"
	          }
	        }
	      }
	    }
	  }
	}

	布尔查询本身也可以用作不评分的查询，简单地将它放置到filter语句中并在内部构建bool逻辑：

	GET lib4/user/_search
	{
	  "query": {
	    "bool": {
	      "must": [
	        {
	          "match": {
	            "title": "how to make millons"
	          }
	        }
	      ],
	      "must_not": [
	        {
	          "match":{
	            "tag":"spam"
	          }
	        }
	      ],
	      "should": [
	        {
	          "match": {
	            "tag": "starred"
	          }
	        }
	      ],
	      "filter": {
	        "bool": {
	          "must":[
	            {
	              "range":{
	                "date":{
	                  "gte":"2015-01-01"
	                }
	              }
	            },
	            {
	              "range":{
	                "price":{
	                  "lte":29.99
	                }
	              }
	            }
	          ],
	          "must_not":[
	            {
	              "term":{
	                "category":"ebooks"
	              }
	            }
	          ]
	        }
	      }
	    }
	  }
	}
		
	GET /lib4/user/_search （布尔查询 名字为”张三“ 或地址或兴趣中包含”喝酒“、名字以”张“开头、名字不是”李四“的记录）
	{
	  "query": {
	    "bool": {
	       "should": [
	         {
	           "term": {
	             "name": {
	               "value": "张三"
	             }
	           }
	         },
	         {
	           "multi_match": {
	             "query": "喝酒",
	             "fields": ["address", "interests"]
	           }
	         }
	       ],
	       "must": [
	         {
	           "match_phrase_prefix": {
	             "name": "张"
	           }
	         }
	       ],
	       "must_not": [
	         {
	           "term": {
	             "name": {
	               "value": "李四"
	             }
	           }
	         }
	       ]
	    }
	  }
	}
	GET /lib4/user/_search （布尔查询 名字为”张三“ 或 名字前缀是”李“的记录，结果以匹配分数字段倒排序）
	{
	  "query":{
	    "bool": {
	      "should": [
	        {
	          "term": {
	            "name": {
	              "value": "张三"
	            }
	          }
	        },
	        {
	          "bool": {
	            "must": [
	              {
	                "match_phrase_prefix": {
	                  "name": "李"
	                }
	              }
	            ]
	          }
	        }
	      ]
	    }
	  },
	  "sort": [
	    {
	      "_score": {
	        "order": "desc"
	      }
	    }
	  ]
	}
		
	i) 聚合查询
	常见的聚合操作：分组、求聚合值
	常用的聚合函数：sum、avg、min、max、cardinality(求基数即互不相同的值的个数) 等。
	
	GET lib4/user/_search （根据年龄进行分组，显示前3条聚合结果，不显示查询的文档）
	{
	  size: 0,
	  "aggs": {
	    "group_of_age": {
	      "terms": {
	        "field": "age",
	        "size": 3
	      }
	    }
	  }
	}
	GET lib4/user/_search （查询所有用户年龄的总和，只显示聚合结果而不显示查询的文档。）
	{
	  "size": 0,
	  "aggs": {
	    "sum_of_age": {
	      "sum": {
	        "field": "age"
	      }
	    }
	  }
	}
	GET lib4/user/_search （查询所有用户年龄的基数，只显示聚合结果而不显示查询的文档）
	{
	  "size": 0,
	  "aggs": {
	    "cardinality_of_age": {
	      "cardinality": {
	        "field": "age"
	      }
	    }
	  }
	}
	GET lib4/user/_search （对于那些有喝酒兴趣的用户按照年龄进行分组，之后再对每一组的年龄求平均值，按照平均年龄进行排序）
	{
	  "query": {
	    "match": {
	      "interests": "喝酒"
	    }
	  },
	  "size": 0, 
	  "aggs": {
	    "group_of_age": {
	      "terms": {
	        "field": "age",
	        "size": 10,
	        "order": {
	          "avg_of_grouped_age": "desc"
	        }
	      },
	      "aggs": {
	        "avg_of_grouped_age": {
	          "avg": {
	            "field": "age"
	          }
	        }
	      }
	    }
	  }
	}
	
	j) contant_score查询
	它将一个不变的常量评分应用于所有匹配的文档。它被经常用于你只需要执行一个filter为没有其他查询（例如评分查询）的情况下。

	GET lib4/user/_search
	{
	  "query": {
	    "constant_score": {
	      "filter": {
	        "term": {
	          "interests": "喝酒"
	        }
	      },
	      "boost": 1.2
	    }
	  }
	}
	
	k) copy_to查询
	ES聚合指定字段时聚合的结果里面只显示聚合的字段。但是在做报表时，我们发现一个问题：如果我们对员工进行聚合，但是我们还希望查看当前员工所在的
	班组，部门等信息。这时如果查询es两次，对于效率来说是不好的。所以我们在这里使用一个es的字段特性：copy_to。
	注意：
	i) 我们copy_to指向的字段字段类型要为text
	ii) text类型字段如果希望进行聚合，设置属性 "fielddata": true（特别注意，设置为fielddata为true的字段将会把数据存放在内存中！）
	iii) copy_to指向的字段不会在head插件查看时显示，但是能通过查询语句作为条件
	
	PUT lib6
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "emp":{
	      "properties": {
	        "first_name":{
	          "type": "text",
	          "copy_to": "full_name"
	        },
	        "last_name":{
	          "type": "text",
	          "copy_to": "full_name"
	        },
	        "full_name":{
	          "type": "text",
	          "fielddata": true
	        }
	        "age":{
	          "type": "integer"
	        }
	      }
	    }
	  }
	}
	PUT lib7
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "emp":{
	      "properties": {
	        "first_name":{
	          "type": "text",
	          "copy_to": "full_name"
	        },
	        "last_name":{
	          "type": "text",
	          "copy_to": "full_name"
	        },
	        "age":{
	          "type": "integer"
	        }
	      }
	    }
	  }
	}
	PUT lib7/emp/_bulk
	{"index":{"_id":1}}
	{"first_name":"san", "last_name":"zhang", "age":23}
	{"index":{"_id":2}}
	{"first_name":"si", "last_name":"li", "age":24}
	{"index":{"_id":3}}
	{"first_name":"wu", "last_name":"wang", "age":25}
	// 使用copy_to字段进行查询
	GET lib7/emp/_search?q=full_name:zhang san
	
	l) 字符串排序的问题
	字符串类型一般分为两类，一种是text（分词，不能用其作为排序或聚合等操作的条件），另一种是keyword（不分词，可以作为排序或聚合等操作的条件）
	解决字符串排序的问题，可以对相关字段索引两次，一次索引分词（用于搜索），一次索引不分词（用于排序或聚合操作）。
	
	PUT lib10
	{
	  "settings": {
	    "number_of_shards": 5,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "user":{
	      "properties": {
	        "name":{
	          "type": "keyword"
	        },
	        "interests":{
	          "type": "text",
	          "fields": {
	            "raw":{
	              "type": "keyword"
	            }
	          }
	        }
	      }
	    }
	  }
	}
	PUT lib10/user/_bulk
	{"index":{"_id":1}}
	{"name":"zhangsan", "interests":"qima shejian"}
	{"index":{"_id":2}}
	{"name":"lisi", "interests":"shufa huihua"}
	{"index":{"_id":3}}
	{"name":"wangwu", "interests":"paobu daqiu"}
	
	GET /lib10/user/_search （注意排序字段使用定义为 keyword 类型的 interests.raw）
	{
	  "query": {
	    "match_all": {}
	  },
	  "sort": [
	    {
	      "interests.raw": {
	        "order": "asc"
	      }
	    }
	  ]
	}
	
	m) 分页查询中的deep paging问题
	GET /lib3/user/_search?from=0&size=2
	GET /lib3/user/_search
	{
	  "from": 0,
	  "size": 2,
	  "query":{
	    "terms":{
	      "interests":["hejiu","changge"]
	    }
	  }
	}
	1) deep paging查询的很深，比如一个索引有三个primary shard，分别存储了6000条数据，我们要得到100页的数据（每一页10条），类似这种问题
	  就称为deep paging。如何得第100页的10条记录？在每一个shard中搜索990-999这10条记录，然后用得到的这30条数据排序，排序之后取前10条数
	  据你可能认为就是我们要搜索的数据，但是很遗憾，这种认识是错误的，因为这3个shard中的数据的_score分数不一样，可能这某一个shard中第一条
	  数据的_score要比另外一个shard中第20条都要低，所以在每个分片中搜索990-999这10条数据然后排序的做法是不对的。
	  正确的做法是每个shard把0-999这1000条数据全部搜索出来（按排序顺序），然后全部返回给协调节点（coordinate node），由协调节点按_score
	  分数排序后，取出第100页的10条数据，然后返回给客户端。
	2) deep paging性能问题：
	  i) 耗费网络带宽，因为搜索过深的话，各shard要把数据传递给协调节点，这个过程是由大量数据传递的。
	  ii) 消耗内存，各shard是要把数据传递给协调节点，这个传递回来的数据是被协调节点保存在内存中的，这样会大量消耗内存。
	  iii) 消耗CPU，协调节点要把传回来的数据进行排序，这个排序过程非常消耗cpu资源。
	  鉴于deep paging的性能问题，所以应该尽量减少使用。
	
	n) 基于scroll技术滚动搜索大量数据
	如果一次要查出比如几十万条数据，那么使用以前的查询方式会存在很严重的性能问题，此时一般会采用scroll滚动查询，一批一批的查，直到所有的数据都查询完为止。
	1) scroll查询会在第一次查询的时候，保存一个当时的视图快照，之后只会基于该旧的视图快照提供数据搜索，如果这个期间数据变更，是不会让用户看到的。
	2) scroll查询会使用基于_doc（不使用_score）进行排序的方式，性能较高。
	3) 每次发送scroll请求，我们都需要指定一个scroll参数，并指定一个时间窗口，只要每次请求在这个时间窗口内完成即可。
	
	查询第一批数据
	GET lib3/user/_search?scroll=1m
	{
	  "query": {
	    "match_all": {}
	  },
	  "sort": [
	    {
	      "_doc": {
	        "order": "asc"
	      }
	    }
	  ],
	  "size": 3
	}
	查询第二批数据（scroll_id的值为查询第一批返回的_scroll_id字段的值）
	GET /_search/scroll
	{
	  "scroll": "1m",
	  "scroll_id": "DnF1ZXJ5VGhlbkZldGNoAwAAAAAAABsLFjQ1Vj...6NzMwdw=="
	}
	
## ES分布式架构简介

	a) ES集群的扩容机制：
	垂直扩容：购置新的服务器已替换原有的服务器
	水平扩容：直接增加普通机器
	集群发现机制（cluster discovery）：比如我们当前启动了一个es进程，当启动了第二个es进程的时候，这个进程作为一个node自动就发现了集群，并加入进去。
	
	b) 负载均衡机制：
	增加或减少节点时，自动均衡分片数据。
	
	c) master节点：
	主节点主要的功能是 创建删除索引时跟踪集群节点的组成，并决定将哪些分片分配给哪些节点。
	稳定的主节点对于集群的健康是非常重要的。
	
	d) 分片和副本机制：
	   i.index包含多个shard。
	   ii.每个shard都是一个最小工作单元，承载部分数据；每个shard都是一个lucene实例，有完整的建立索引和处理请求的能力。
	   iii.增加节点时，shard会自动在nodes中进行负载均衡。
	   iv.主分片和副本分片，每个shard只存在一个primary shard及其对应的若干replica shard（默认是5个分片和1个副本）。
	      primary shard的数量在创建索引的时候就已经固定了，replica shard的数量可以随时修改。primary负责写并同步，replica负责容错以及读请求负载的分担。
	      primary shard不能和相同分片的replica shard放在同一个节点上，否则节点宕机之后，主分片和副本分片的数据都会丢失，起不到的容错的作用。
	      当集群的节点数量小于或等于分片的副本数时，那么集群就会处于亚健康状态，GET _cat/health 的状态显示为 yellow。
	   v.请求的路由，ES集群中每个节点都是对等的（不管是master节点或slave节点，都可以处理客户端请求），当请求到达节点1，节点1会自动路由到相关节点获取数据。
	
	e) 集群的容错过程：
	
        P1  P2  R3[2]         R1[1]  R2[2]  R3[1]           P3  R2[1]  R1[2]
        ------------          -------------------           ----------------
           master                   slave                         slave
             |________________________|_____________________________|	
                             
                             ||
                             || master宕机
                            \||/
                             \/
			
                               P1.  R2[2]  R3[1]             P3  P2.  R1[2]
                               -----------------             --------------
                                     master.                      slave
                                       |____________________________|
                             
                             ||
                             || 原master节点重启
                            \||/
                             \/
                                       
        R1[1].  R2[1].  R3[2].     P1.  R2[2]  R3[1]             P3  P2.  R1[2]
        ---------------------      -----------------             --------------
             slave.                     master.                       slave
               |__________________________|_____________________________|
               		
	   假设某个时刻，上述集群的master出现宕机，那么此时集群的状态是red（因为并不是所有的primary shard都是活跃的）。那么集群的容错过成如下：
	   1) 重新选举另外一个节点作为master。
	   2) 选举出的新的master会把丢失的primary shard的其中一个副本提升为primary shard，此时所有的primary shard都是活跃的，集群的状态由
	      red变为yellow（因为并不是所有的副本都是活跃的）。
	   3) 宕机的服务器重启之后，master会把每个primary shard的数据拷贝一份到该重启的服务器上。此时primary shard和replica shard都是活跃的，
	      集群的状态由yellow重新变为green。
	      
## ES文档的核心元数据
	
	a) _index
	同一个索引下存放的是同一类文档（ES6.0以后规定每一个索引下只能定义一个类型）。
	创建索引时，索引名必须是小写的，不能以下划线开头，不能包含逗号。
	
	b) _type
	表示当前文档属于索引中的哪个类型（再次强调，ES6.0以后规定每一个索引下只能定义一个类型）。
	类型名大小写随意，不能以下划线开头，不能包含逗号。
	
	c) _id
	文档的唯一标识，和索引、类型组合在一起，唯一标识一个文档。
	可以手动指定文档_id的值，也可以由ES来自动生成这个值。
	
	d) _source
	表示每个文档在被添加时的那些字段和字段值集合。
	GET lib3/user/_search?q=interests:music&sort=age:asc&_source=name,age
	
## ES文档数据路由的原理

	一个索引有多个分片构成，当添加、修改、删除、查询一个文档时，es就要决定这个文档存储在哪个分片上，这个过程就称为数据路由（routing）。
	ES数据路由的算法：
		
		shard = hash(routing) % number_of_primary_shards
	
	每次增删改查时都会有一个routing值，默认为文档的_id值，也可以通过 ?routing=xxx 参数手动指定一个值，手动指定对于负载均衡以及
	批量读取的性能都可能有帮助。
	
## 写一致性原理和quorum机制(增删改操作)
	
	任何一个增删改操作都可以跟上一个参数 consistency（取值枚举为 quorum|one|all）
	quorum：默认值，当大部分shard是活跃的才能执行（计算公式为 int( (primary+number_of_replica)/2 )+1，例如3个主分片和1个副本，那么法定值就是3）
	one：只要有一个primary shard是活跃的，就可以执行
	all：所有的primary shard和所有的replica shard是活跃的才能够执行
	
	注意，如果consistency为默认的quorum，可能会出现shard不能分配齐全的情况。比如，1个primary shard和1个replica shard，法定值为2，但是因为相同数据
	的主分片和副本分片不能在同一个节点上，所以仍然不能执行增删改操作。
	
	最后，当活跃的shard的个数没有达到要求的时候，es默认会等待一分钟。如果在等待的时间内活跃的shard的个数没有增加，则显示timeout。超时时间可以如下设置：
	PUT xxxindex/xxxtype/xxxid?timeout=60s  （单位可以为 s|ms，即秒或者毫秒）
	
## 文档查询的内部原理（读操作）

	1) 查询请求发送给任意一个节点，该节点就成了 coordinating node（协调节点），该节点使用路由算法计算出目标文档所在的primary shard。
	2) 协调节点把请求转发给 primary shard 或 对应的 replica shard（使用轮询调度算法round-robin-scheduling把请求平均分配至主节点和副本节点）。
	3) 处理请求的节点将结果返回给协调节点，再由协调节点发回给应用程序。
	特殊情况：请求的文档还在建立索引的过程中，primary shard上存在，但replica shard上不存在。如果请求被转发到replica shard上，这时就提示找不到该文档。
	可以指定查询的超时时间以增强客户体验：
	GET xxxindex/xxxtype/xxxid?timeout=60s  （单位可以为 s|ms，即秒或者毫秒）
	
## 如何计算相关度分数

	使用的是TF-IDF算法 (term frequency & inverse document frequency)，即词频-反转文档频率算法。
	在文本挖掘和自然语言处理方面，tf-idf是非常重要也非常常用的算法。
	基本的思想：如果某个词或短语在一篇文章中出现的频率TF高，并且在其他文章中很少出现，则认为此词或者短语具有很好的类别区分能力，适合用来分类。
	
	tf：词频，是指某个词在某篇文章中出现的频率。比如，某篇文章共1000个词汇，其中hello出现5次，那么其tf=5/1000。
	    tf最直观的理解就是，当一个词在本文中出现的频率越高，则这篇文章的主题和这个词的相关可能性越大。
	    这种直观理解是否准确呢？可以说相当不准确。举例来说，一篇文章中出现最多的字词可能是你、我、他、的、是、这、那等等。通过这些词来分析一篇文章
	    的内涵几乎是不可能的。所以人们又做了进一步处理。就是把这些在每篇文章里都可能大量出现又和文章意义关联不大的词都去掉。这类词也有了一个专有名
	    称：停用词。所以文本处理的前几步通常都包括这一步：去停用词，既能减少词汇处理量，又能有效减少歧义。属于重要的预处理步骤。
	    去掉停用词之后的词频是否就能比较准确的表达文章含义了呢？还是不够的。设想一下，如果一篇文章是描述一份国内专利的。文章里反复提到了“中国”两个
	    字。中国这、中国那，结果中国这个词的词频最高，那么这个词和实际要说的专利有多大关联呢？基本没有。但是我们又不能把中国加到停用词里，否则停用
	    词列表就太多了，而且去掉也不合理，万一某篇文章就是介绍中国的呢。这个时候就又发明另外一个算法：idf。
	
	idf：反文档频率，目的就是针对刚才说的这种情况进行识别。还以上面为例，这篇文章中“中国”这个词的词频最高，却不能反应真实的文章内涵，这是为什么呢？
	    很大程度是因为“中国”这个词太常见了，不仅在这篇文章里出现次数多，在其他文章里出现的次数也很多。这么一来，说明这个词不足以描述文章“特征”。于
	    是评价某个词的“独特性”的公式idf就这样设计出来：语料库文章总数/包含某个词的文章数。意味着，如果一个词在越多的文章中出现过，那么其“独特性”
	    就越低。出现的文章数越少，idf值越大，其独特性越高。整体思路就是这样，后续再加上一点数学上的处理：如果一个词在所有语料库的文章中都没出现过。
	    那么分母就是0了，这在计算中会发生错误，所以往往把分母+1，保证其至少不会是0。虽然缺失了一点点精确性，但保证计算过程不至于出错。而且在语料库
	    文章数量很大时，对结果的影响是微乎其微的。另外，这个除法除出来的结果可能差别很大，有的接近1（几乎每篇文章都出现这个词）、有的非常大（极少出
	    现），这时候看起来值的差距太悬殊，不易计算也不易比较。于是再取一下对数。所以整个公式就是：
	    	
                                               |D|
                         idf_i = log ------------------------  (分子D表示语料库中的文件总数，分母表示包含该词语的文件的数目+1)
                                       |{j: t_i ∈ d_j}| + 1
                                       
	    最后，再把tf和idf相乘，这个得出来的值就很能反映文章的主题了。举个例子，现有文章库100万篇。当对一篇新来的文章进行分析时，发现其tf排名第一
	    的是“中国”，30/1000(假定去掉停用词之后还有1000个词汇)=0.03，tf排名第二的是“青蒿素”，20/1000=0.02，再继续计算idf，发现包含“中国”的文
	    章有10万篇，其idf=log(1000000/100000)=1；含有“青蒿素”的文章只有1000篇，其idf=log(1000000/1000)=3，最后“中国”的tf-idf值为0.03，
	    “青蒿素”的tf-idf值为0.06。这样，如果按tf-idf值排序，尽管“中国”出现的次数多，但仍被排到“青蒿素”之后。说明这篇文章和青蒿素相关的可能性较
	    大。当我们选取tf-idf值排名前若干的词汇作为一篇文章的主旨，可靠性就相对准确多了。
	
	field-length norm：归一化字段长度，在ES中评价文档相关性，除了上面提到的两个因子，我们通常还会考虑field-length norm。ES的底层使用lucene
	    索引，lucene认为短字段比长字段具有更多的权重。最直观的立即就是标题的字段要比文章内容的字段含有更大的比重。总结起来就是 field越长相关度越
	    弱，field越短相关度强。
	    
	查看 _score 是如何被计算出来的：
	GET lib3/user/_search?explain=true
	{
	  "query": {
	    "match": {
	      "interests": "music"
	    }
	  }
	} 
	
	查看_id为2的用户是否符合查询条件：
	GET lib3/user/2/_explain
	{
	  "query": {
	    "match": {
	      "interests": "music"
	    }
	  }
	}  
	
## Doc Values
	
	DocValues其实是lucene在构建倒排索引的时，会额外建立一个有序的正排索引（基于 document => field value 的映射列表）。那么正排索引是什么样的呢？
	假设现在我们的索引类型中有如下两条数据：
	{"birthday":"1995-11-11", age:23}
	{"birthday":"1994-09-09", age:24}
	那么其对应的正排索引则为：
	document    age    birthday
	doc1        23     1995-11-11
	doc2        24     1994-09-09
	正排索引对于排序、分组和一些聚合操作能够大大提升性能。
	注意：正排索引对于不分词的字段（数字类型、日期类型、keyword类型的字符串等）是开启的，对于分词的text字段无效（除非把fielddata设置为true）。
	
	PUT lib11 （创建用户索引，为address建立正排索引 和 倒排索引并预加载，不为age字段建立正排索引）
	{
	  "settings": {
	    "number_of_shards": 3,
	    "number_of_replicas": 0
	  },
	  "mappings": {
	    "user":{
	      "properties": {
	        "name":{
	          "type": "keyword"
	        },
	        "address":{
	          "type": "text",
	          "fields": {
	            "raw":{
	              "type": "keyword"
	            }
	          },
	          "fielddata": true
	        },
	        "age":{
	          "type": "integer",
	          "doc_values": false
	        }
	      }
	    }
	  }
	}
	PUT lib11/user/_bulk
	{"index":{"_id":1}}
	{"name":"zhangsan", "address":"beijing haidian wudaokou", "age":23}
	{"index":{"_id":2}}
	{"name":"lisi", "address":"beijing haidian huilongguan", "age":24}
	{"index":{"_id":3}}
	{"name":"wangwu", "address":"beijing fengtai lianhuaqiao", "age":25}
	
	GET lib11/user/_search （这时候如果再使用age进行排序则会报错）
	{
	  "query": {
	    "match_all": {}
	  },
	  "sort": [
	    {
	      "age": {
	        "order": "desc"
	      }
	    }
	  ]
	}
	
	GET lib11/user/_search （查出的结果根据 address.raw 排序，ok）
	{
	  "query": {
	    "match_all": {}
	  },
	  "sort": [
	    {
	      "address.raw": {
	        "order": "asc"
	      }
	    }
	  ]
	}   
	
				
		
		
		
		
		