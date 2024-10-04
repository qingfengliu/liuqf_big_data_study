package org.liuqf_test_hive_udaf01;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.util.StringUtils;

import java.util.ArrayList;

/***
 * @author Saodiseng
引用自:https://blog.csdn.net/qq_41018861/article/details/117150704
这是一个求均值的udaf函数,其实就是一个mapreduce过程。
可见写udaf就是写mapreduce
 ***/

@Description(name = "myavg", value = "_FUNC_(x) - Returns the mean of a set of numbers")
public class Juhe extends AbstractGenericUDAFResolver {

    public static final Log LOG = LogFactory.getLog(Juhe.class.getName());

    /**
     * 读入参数类型校验，满足条件时返回聚合函数数据处理对象
     * 这里改动了,无论如何都会调用udaf过程
     */
    @Override
    public GenericUDAFEvaluator getEvaluator(TypeInfo[] info) throws SemanticException {
        return new GenericUDAFAverageEvaluator();
    }

    /**
     * GenericUDAFAverageEvaluator.
     * 自定义静态内部类：数据处理类，继承GenericUDAFEvaluator抽象类
     */
    public static class GenericUDAFAverageEvaluator extends GenericUDAFEvaluator {

        //1.1.定义全局输入输出数据的类型OI实例，用于解析输入输出数据
        // input For PARTIAL1 and COMPLETE
        PrimitiveObjectInspector inputOI;

        StructObjectInspector soi;
        StructField countField;
        StructField sumField;
        LongObjectInspector countFieldOI;
        DoubleObjectInspector sumFieldOI;

        //1.2.定义全局输出数据的类型，用于存储实际数据
        // output For PARTIAL1 and PARTIAL2
        Object[] partialResult;

        // output For FINAL and COMPLETE
        DoubleWritable result;


        /*
         * .这个其实就是中间处理结果的数据结构。merge的传入数据就是这个
         *
         */
        static class AverageAgg implements AggregationBuffer {
            long count;
            double sum;
        };

        //一看new就是的，这个是个全局量。用于传递结果
        @Override
        public AggregationBuffer getNewAggregationBuffer() throws HiveException {
            AverageAgg result = new AverageAgg();
            result.count = 0;
            result.sum = 0;
            return result;
        }

        @Override
        public void reset(AggregationBuffer aggregationBuffer) throws HiveException {
            AverageAgg myagg = (AverageAgg) aggregationBuffer;
            myagg.count = 0;
            myagg.sum = 0;
        }

        /*
         * 初始化：对各个模式处理过程，提取输入数据类型OI，返回输出数据类型OI
         * .每个模式（Mode）都会执行初始化
         * 定义数据处理函数输入、输出值的类型
         * 并且帮助数据处理函数识别输入函数、装载数据输出
         * 1.输入参数parameters：
         * 2.返回值OI：
         */
        //程序在集群执行->PrimitiveObjectInspector(map输入的数据类型)->iterate->AggregationBuffer(输出的buffer数据放在这里)->terminatePartial->AggregationBuffer(输出的buffer数据放在这里)=PrimitiveObjectInspectorFactory(定义输出类型)
        //->程序在集群执行->StructObjectInspector(reduce的输入类型)->merge->AggregationBuffer(输出的buffer数据放在这里)->terminate->PrimitiveObjectInspectorFactory定义输出类型
        //—>程序在集群执行->输出结果
        @Override
        public ObjectInspector init(Mode mode, ObjectInspector[] parameters) throws HiveException {
            assert (parameters.length == 1);
            super.init(mode, parameters);

            // 定义怎样从map、reduce传入的数据的类型得到传入的数据。我认为是一种反射+自省
            if (mode == Mode.PARTIAL1 || mode == Mode.COMPLETE) {
                //PrimitiveObjectInspector用于识别从数据源里出来的数据.iterate(对应map阶段)的传入数据是从数据源直接来的。
                //在iterate函数内使用PrimitiveObjectInspectorUtils.getDouble(p, inputOI);可以得到数据
                //PrimitiveObjectInspectorUtils帮助自省
                inputOI = (PrimitiveObjectInspector) parameters[0];//反射,inputOI知道了自己是谁
            } else {
                // StructObjectInspector用于识别中间处理结果的数据、merge(对应reduce和combine阶段)的传入数据是中间处理结果
                // 其实反射的结果就是AverageAgg。

                //agg.count += countFieldOI.get(soi.getStructFieldData(partial, countField));
                //agg.sum += sumFieldOI.get(soi.getStructFieldData(partial, sumField));


                soi = (StructObjectInspector) parameters[0];
                countField = soi.getStructFieldRef("count");
                sumField = soi.getStructFieldRef("sum");
                //数组中的每个数据，需要其各自的基本类型OI实例解析
                countFieldOI = (LongObjectInspector) countField.getFieldObjectInspector();
                sumFieldOI = (DoubleObjectInspector) sumField.getFieldObjectInspector();
            }

            // 输出terminatePartial、terminate
            if (mode == Mode.PARTIAL1 || mode == Mode.PARTIAL2) {
                // 这段代码定义的是terminatePartial的返回值(输出)的类型。
                // 并且将terminatePartial函数中用于装载数据的partialResult给初始化
                // foi 就是装载到partialResult数组里边的数据的类型
                // foi 其实就是soi,但是给包装了将PrimitiveObjectInspectorFactory包装成了StructObjectInspector

                partialResult = new Object[2];
                partialResult[0] = new LongWritable(0);
                partialResult[1] = new DoubleWritable(0);
                /*
                 * .构造Struct的OI实例，用于设定聚合结果数组的类型
                 * .需要字段名List和字段类型List作为参数来构造
                 */
                ArrayList<String> fname = new ArrayList<String>();
                fname.add("count");
                fname.add("sum");
                ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();
                //注：此处的两个OI类型 描述的是 partialResult[] 的两个类型，故需一致
                foi.add(PrimitiveObjectInspectorFactory.writableLongObjectInspector);
                foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
                return ObjectInspectorFactory.getStandardStructObjectInspector(fname, foi);
            } else {
                //就是terminate的结果的类型。也得包装下,用于给hive返回结果
                result = new DoubleWritable(0);
                return PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;
            }
        }




        boolean warned = false;

        /*
         * .遍历处理原始数据
         */
        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters) throws HiveException {
            assert (parameters.length == 1);
            AverageAgg myagg = (AverageAgg) agg;
            Object p = parameters[0];
            if (p != null) {
                try {
                    //通过基本数据类型OI解析Object p的值
                    double v = PrimitiveObjectInspectorUtils.getDouble(p, inputOI);
                    //java都是引用赋值(除了基础数据类型)所以操作myagg就是agg。agg是传给下个阶段的数据.但是这里给他改了，要不看着多费劲。换个名字没必要
                    myagg.count++;
                    myagg.sum += v;
                } catch (NumberFormatException e) {
                    if (!warned) {
                        warned = true;
                        LOG.warn(getClass().getSimpleName() + " "
                                + StringUtils.stringifyException(e));
                        LOG.warn(getClass().getSimpleName()
                                + " ignoring similar exceptions.");
                    }
                }
            }
        }

        /*
         * .得出部分聚合结果。有完整的mapreduce的map阶段和combine阶段需要中途保存的结果
         */
        @Override
        public Object terminatePartial(AggregationBuffer agg) throws HiveException {
            AverageAgg myagg = (AverageAgg) agg;
            ((LongWritable) partialResult[0]).set(myagg.count);
            ((DoubleWritable) partialResult[1]).set(myagg.sum);
            return partialResult;
        }


        /*
         * .聚合得出聚合结果。
         * .注：Object[] 是 Object 的子类，此处 partial 为 Object[]数组，
         *	有完整的mapreduce阶段的combine和reduce
         */
        @Override
        public void merge(AggregationBuffer agg, Object partial) throws HiveException {
            if (partial != null) {
                AverageAgg myagg = (AverageAgg) agg;
                //通过StandardStructObjectInspector实例，分解出 partial 数组元素值
                Object partialCount = soi.getStructFieldData(partial, countField);
                Object partialSum = soi.getStructFieldData(partial, sumField);
                //通过基本数据类型的OI实例解析Object的值
                myagg.count += countFieldOI.get(partialCount);
                myagg.sum += sumFieldOI.get(partialSum);
            }
        }

        /*
         * .得出最终聚合结果
         */
        @Override
        public Object terminate(AggregationBuffer agg) throws HiveException {
            AverageAgg myagg = (AverageAgg) agg;
            if (myagg.count == 0) {
                return null;
            } else {
                result.set(myagg.sum / myagg.count);
                return result;
            }
        }
    }
}
