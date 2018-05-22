package other;

/**
 * Created by ZJYYY on 2018/5/18.
 */


public class EchoServiceImpl implements EchoService {

    //接口类的实现
    @Override
    public String echo(String ping) {
        String aaa = ping != null ? ping + " --> I am ok." : " I am terrible :( !!!";
        return aaa;
    }
}

