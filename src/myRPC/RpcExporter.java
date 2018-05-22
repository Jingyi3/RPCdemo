package myRPC;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ZJYYY on 2018/5/18.
 * //RPC服务端服务发布者
 * 发布者的主要职责
 * 1.作为服务器，监听客户端的TCP链接，接收到新的客户端链接之后，将其封装成Task，有现成池执行。
 * 将客户端发送的码流反序列化成对象，反射调用服务是闲着，获取执行结果。
 * 将执行结果对象反序列化，通过Socket发送给客户端。
 * 远程服务调用完成之后，释放Socket等连接资源，防止句柄泄漏。
 */
public class RpcExporter {
    //RPC服务端服务发布者
    static Executor executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    public static void exporter(String hostName, int port) throws Exception {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                executor.execute(new ExporterTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }

    private static class ExporterTask implements Runnable {
        Socket client = null;

        public ExporterTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                input = new ObjectInputStream(client.getInputStream());
                String interfaceName = input.readUTF();
                Class<?> service = Class.forName(interfaceName);
                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Method method = service.getMethod(methodName, parameterTypes);
                Object result = method.invoke(service.newInstance(), arguments);
                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

