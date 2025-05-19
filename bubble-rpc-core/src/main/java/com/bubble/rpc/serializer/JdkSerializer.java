package com.bubble.rpc.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{
    /**
     * 序列化
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {

        //创建一个字节数组输出流，用于存储序列化之后的数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        try {
            // 将对象写入输出流 (序列化)
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            // 返回序列化后的字节数组
            return outputStream.toByteArray();
        } finally {
            // 关闭输出流，释放资源
            objectOutputStream.close();
        }
    }

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            return (T) objectInputStream.readObject();
        }catch (ClassNotFoundException e){
            throw new IOException("class not found");
        }finally {
            objectInputStream.close();
        }
    }
}
