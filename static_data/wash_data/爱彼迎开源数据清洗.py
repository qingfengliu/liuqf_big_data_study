import pandas as pd
from pyhive import hive

from pyspark import SparkContext
from pyspark import SparkConf
from pyspark.sql import SparkSession
#爱彼迎数据没法直接放到hive上,需要吧双引号之间的逗号变成
#读取数据

data = pd.read_csv('/home/hadoop/data/listings.csv')
#将所有列都打印出来
pd.set_option('display.max_columns', None)

#host_verifications,amenities
# print(data.head())
print(data.shape)

#暂时把host_verifications,amenities列去掉。
data.drop(['host_verifications','amenities'],axis=1,inplace=True)



conf = SparkConf().setMaster('local[*]').setAppName('pandas_to_hive')
sparksession = SparkSession.builder.config(conf=conf).config("hive.metastore.uris","thrift://hadoop1:9083").enableHiveSupport().getOrCreate()

df = sparksession.createDataFrame(data)
df.registerTempTable('test_hive')
sparksession.conf.set('hive.exec.dynamic.partition', 'True')
sparksession.conf.set('set hive.exec.dynamic.partition.mode', 'nonstrict')


sparksession.sql('insert overwrite {} partition(dt="2024-10-04") '
                 'select * from test_hive'.format("test.aby_albany_listings"))

sparksession.sql('select * from test.aby_albany_listings limit 10').show()
sparksession.stop()



data = pd.read_csv('/home/hadoop/data/reviews.csv')

print(data.shape)

conf = SparkConf().setMaster('local[*]').setAppName('pandas_to_hive')
sparksession = SparkSession.builder.config(conf=conf).config("hive.metastore.uris",
                                                             "thrift://hadoop1:9083").enableHiveSupport().getOrCreate()

df = sparksession.createDataFrame(data)
df.registerTempTable('test_hive_reviews')
sparksession.conf.set('hive.exec.dynamic.partition', 'True')
sparksession.conf.set('set hive.exec.dynamic.partition.mode', 'nonstrict')

sparksession.sql('insert overwrite {} partition(dt="2024-10-04") '
                 'select * from test_hive_reviews'.format("test.aby_albany_reviews"))

sparksession.sql('select * from test.aby_albany_reviews limit 10').show()
sparksession.stop()

#保存成csv文件,并且不要索引
# data.to_csv(r'D:\data\爱彼迎开源数据\listings_washing.csv',index=False)

# conn = hive.Connection(host='192.168.212.133', port=10000, username='', database='test')
#
# cursor = conn.cursor()
#
# cursor.execute('select * from test.aby_albany_calendar limit 10')
# print(cursor.fetchall())
#
# # 插入数据
#
#
# cursor.close()
# conn.close()

#load data local inpath '/home/hadoop/data/reviews.csv' overwrite into table test.aby_albany_reviews partition (dt='2024-10-04')
#字符串中的双引号之间的逗号.建表的时候使用ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
