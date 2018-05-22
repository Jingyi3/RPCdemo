package myRPC;

import java.net.InetSocketAddress;

/**
 * Created by ZJYYY on 2018/5/18.
 *
 * 首先创建一个异步发布服务端的线程并启动，用于接收RPC客户端的请求，根据请求参数调用服务实现类，返回结果给客户端。
 * 随后创建客户端服务代理类，构造RPC请求参数，发起RPC调用，将调用将结果输出
 */
public class RpcTest {
    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RpcExporter.exporter("localhost", 8080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        RpcImporter<EchoService> importer = new RpcImporter<EchoService>();
        EchoService echo = importer.importer(EchoServiceImpl.class,
                new InetSocketAddress("localhost", 8080));
        System.out.println(echo.echo("Are you ok ?"));
    }
}
