package myRPC;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.*;

/**
 * Created by ZJYYY on 2018/5/18.
 * <p>
 * RPC客户端本地服务代理
 * <p>
 * 主要功能
 * 1.本地的接口调用转换成JDK的动态代理，在动态代理中实现接口的远程调用
 * 2.床架socket客户端，根据指定地址链接远程服务提供者
 * 3.将远程服务调用所欲的接口类，方法名，参数列表等编码后发送给服务端提供者
 * 4.同步阻塞等待服务端返回应答，获取应答之后返回
 */
public class RpcImporter<S> {
    public S importer(final Class<?> serviceClass, final InetSocketAddress addr) {
        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass.getInterfaces()[0]},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        Socket socket = null;
                        ObjectOutputStream output = null;
                        ObjectInputStream input = null;
                        try {
                            socket = new Socket();
                            socket.connect(addr);
                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeUTF(serviceClass.getName());
                            output.writeUTF(method.getName());
                            output.writeObject(method.getParameterTypes());
                            output.writeObject(args);

                            input = new ObjectInputStream(socket.getInputStream());
                            return input.readObject();
                        } finally {
                            if (socket != null) {
                                socket.close();
                            }
                            if (output != null) {
                                output.close();
                            }
                            if (input != null) {
                                input.close();
                            }
                        }
                    }
                });
    }
}
