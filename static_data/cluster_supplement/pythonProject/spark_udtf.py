from pyspark.sql import SparkSession
from pyspark.sql.functions import udtf
from pyspark.sql.types import Row

@udtf(returnType="key: string,word: string")
class WordSplitter:
    def eval(self, text: Row):
        #每8个字符串切分一次
        text=text['listing_id']
        for i in range(0, len(text), 8):
            yield (text,text[i:i+8])

@udtf(returnType="word: string")
class WordSplitter2:
    def eval(self, text: str):
        #每8个字符串切分一次
        for i in range(0, len(text), 8):
            yield (text[i:i+8],)


spark=SparkSession.builder      \
        .appName("SparkByExamples.com")      \
        .master("spark://192.168.212.133:7077")                 \
        .config("hive.metastore.uris", "thrift://hadoop1:9083") \
        .enableHiveSupport().getOrCreate()


# Register the UDTF for use in Spark SQL.
spark.udtf.register("split_words", WordSplitter)
spark.udtf.register("split_words2", WordSplitter2)

spark.sql("select * from split_words(TABLE(SELECT * FROM test.test_udtf_from_calendar))").show()

spark.sql("SELECT listing_id,word FROM test.test_udtf_from_calendar, LATERAL split_words2(listing_id)").show()


spark.stop()
