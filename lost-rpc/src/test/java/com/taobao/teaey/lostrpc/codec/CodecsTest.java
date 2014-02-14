package com.taobao.teaey.lostrpc.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by xiaofei.wxf on 14-2-14.
 */
public class CodecsTest {
    private static final DatePojo datePojo = new DatePojo();
    private static final Pojo obj = new Pojo();
    @Before
    public void before(){
        System.out.println("BEFORE:" + datePojo);
    }
    @Test
    public void testKryoCodec() throws Exception {
        byte[] bytes = KryoCodec.INSTANCE.encode(obj);
        Object obj = KryoCodec.INSTANCE.decode(bytes);
        System.out.println(obj);


        System.out.println(System.getProperty("java.vm.name"));
    }

    @Test
    public void outKryo() throws Exception {
        byte[] bytes = KryoCodec.INSTANCE.encode(obj);
        FileOutputStream fileOutputStream = new FileOutputStream("D:/obj.bin");
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
    @Test
    public void loadKryo() throws Exception {
        FileInputStream in = new FileInputStream("D:/obj.bin");
        byte[] data = new byte[in.available()];
        in.read(data);
        in.close();

        Object obj = KryoCodec.INSTANCE.decode(data);
        System.out.println(obj);
    }

    @Test
    public void testHessianCodec() throws Exception {
        byte[] bytes = HessianCodec.INSTANCE.encode(datePojo);
        System.out.println(HessianCodec.INSTANCE.decode(bytes));
    }
    @Test
    public void testJavaCodec() {
        try {
            byte[] data = JavaCodec.INSTANCE.encode(datePojo);
            System.out.println(JavaCodec.INSTANCE.decode(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFastjsonCodec() {
        try {
            byte[] data = JsonCodec.INSTANCE.encode(datePojo);
            System.out.println(JsonCodec.INSTANCE.decode(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void json() {
        Gson gson = new Gson();
//        System.out.println(gson.toJson(datePojo));
//        System.out.println(JSON.toJSONString(datePojo));
        System.out.println(gson.toJson(obj));
        System.out.println(JSON.toJSONString(obj));
        System.out.println(JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteClassName));
        System.out.println(JSON.toJSONString(obj, SerializerFeature.UseISO8601DateFormat));
    }
}
