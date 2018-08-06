
create database if not exists tv;

create table if not exists tv.tvcount(
	tvid varchar(50) not null primary key,
	tvname varchar(255),
	director varchar(128),
	actor varchar (128),
	allnumber varchar(20),
	tvtype varchar(30),
	description text,
	alias varchar(100),
	tvshow varchar(20),
	present varchar(20),
	score varchar(10),
	zone varchar(20),
	commentnumber varchar(20),
	supportnumber varchar(20),
	pic varchar(100)
);


insert into tv.tvcount(tvid, tvname, director, 
			actor, allnumber, tvtype, 
			description, 
			alias, tvshow, present, 
			score, zone, commentnumber, 
			supportnumber, pic)
values("id_zb869e6568e9311e6b32f", "剧集：奇星记之鲜衣怒马少年时", "黄祖权",
		"吴磊/陈翔/张予曦", "51", "剧情/古装",
		"简介：天地玄黄，生生不息，随着历史时光交替，诸多秘密尘封于世界八方，伴随着它们的故事代代相传，这些故事或神奇玄妙，或曲折惊奇，吸引着一代又一代好奇的人们一探究竟。传说在每一次天下大乱之际，都会有天降奇星，重建秩序。时至今日，历史重演。一场举国皆知的流星雨过后，当朝年轻皇帝派亲信少年白泽前往塞外找寻天降奇石，希望能以奇石的无穷力量排除奸妄，安定天下。白泽才识过人，正直善良，以国为家，此次他决心完成这个关系到江山社稷的秘密任务，恢复家族荣誉。任务途中白泽结识了鬼灵精怪的顽劣少年展雄飞，以及跟班小胖元帅，于是三个少年一路并肩作战。前途曲折，不仅有各路枭雄对流星石的虎视眈眈，还有朝堂之上的佞臣陷害追杀，危机重重。",
		"王的密探", "1574489972", "2017-01-03",
		"7", "中国", "48252",
		"623112", "http://r1.ykimg.com/05160000594A2C03859B5D0D0A0057B4"
);


insert into tv.tvcount(tvid, tvname, director, 
			actor, allnumber, tvtype, 
			description, 
			alias, tvshow, present, 
			score, zone, commentnumber, 
			supportnumber, pic)
values("id_z9cd2277647d311e5b692", "剧集：微微一笑很倾城", "林玉芬",
		"杨洋/郑爽/毛晓彤", "30", "剧情/都市/爱情",
		"美女学霸贝微微，立志成为游戏工程师，化名“芦苇微微”跻身网游高手，因拒绝上传真实照片而惨遭侠侣“真水无香”无情抛弃，却意外得到江湖第一高手“一笑奈何”的垂青。为了赢得“侠侣挑战赛”，贝微微欣然答应与“一笑奈何”结盟并组队参赛。两人一路闯荡江湖早已心灵相通，可贝微微做梦也没想到，一路出生入死的伙伴竟然就是同校风云人物——师兄肖奈。无论线上还是线下，肖奈都是能力出众的“大神”，更巧的是，他竟然就是游戏开发测试的负责人。线上是侠侣队友，线下是工作伙伴，缘分就是这么妙不可言。当贝微微发现这一真相的时候，两人就自然而然地在一起了。",
		"微微一笑很倾城电视剧版/A Smile Is Beautiful", "24155009936", "2016-08-22",
		"7.7", "中国", "1985978",
		"20530147", "http://r1.ykimg.com/0516000059488937ADBA1F9712028679"
);





