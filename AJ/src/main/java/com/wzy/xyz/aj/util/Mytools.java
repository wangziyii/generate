package com.wzy.xyz.aj.util;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;

import java.util.ArrayList;

public class Mytools {

    public static final String JMETER_ENCODING = "UTF-8";

    // 因为我们就模拟一条请求，所以这个线程数先设置成1
    public static final int NUMBER_THREADS = 1;

    /** 执行结果输出的日志 */
    public static final String replayLogPath = "/Users/liufei/Downloads/jmter/replay_result.log";

    /** 生成的jmx的地址 */
    public static final String jmxPath = "/Users/liufei/Downloads/jmter/test.jmx";


    //创建测试计划
    private static TestPlan getTestPlan() {
        TestPlan testPlan = new TestPlan("Test Plan");
        testPlan.setFunctionalMode(false);
        testPlan.setSerialized(false);
        testPlan.setTearDownOnShutdown(true);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setProperty(new BooleanProperty(TestElement.ENABLED, true));
        testPlan.setProperty(new StringProperty(TestElement.COMMENTS, ""));
        testPlan.setTestPlanClasspath("");
        Arguments arguments = new Arguments();
        testPlan.setUserDefinedVariables(arguments);
        return testPlan;
    }


    //创建线程组
    /***
     * 创建线程组
     * @param loopController 循环控制器
     * @param numThreads 线程数量
     * @return
     */
    private static ThreadGroup getThreadGroup(LoopController loopController, int numThreads) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(1);
        threadGroup.setDelay(0);
        threadGroup.setDuration(0);
        threadGroup.setProperty(new StringProperty(ThreadGroup.ON_SAMPLE_ERROR, "continue"));
        threadGroup.setScheduler(false);
        threadGroup.setName("回放流量");
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        threadGroup.setProperty(new BooleanProperty(TestElement.ENABLED, true));
        threadGroup.setProperty(new TestElementProperty(ThreadGroup.MAIN_CONTROLLER, loopController));
        return threadGroup;
    }

    //创建循环控制器
    private static LoopController getLoopController() {
        LoopController loopController = new LoopController();
        loopController.setContinueForever(false);
        loopController.setProperty(new StringProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName()));
        loopController.setProperty(new StringProperty(TestElement.TEST_CLASS, LoopController.class.getName()));
        loopController.setProperty(new StringProperty(TestElement.NAME, "循环控制器"));
        loopController.setProperty(new StringProperty(TestElement.ENABLED, "true"));
        loopController.setProperty(new StringProperty(LoopController.LOOPS, "1"));
        return loopController;
    }

    //简化的http请求
    public static HTTPSamplerProxy getHttpSamplerProxy(
            String samplerName,String samplerDomain,int samplerPort,String samplerMethod,String samplerPath){

        HTTPSamplerProxy Sampler = new HTTPSamplerProxy();
        Sampler.setName(samplerName);
        Sampler.setDomain(samplerDomain);
        Sampler.setPort(samplerPort);
        Sampler.setPath(samplerPath);
        Sampler.setMethod(samplerMethod);
        Sampler.setContentEncoding("UTF-8");
        Sampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        Sampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return Sampler;
    }



    //创建http请求
    /***
     * 创建http请求信息
     * @param url ip地址
     * @param port 端口
     * @param api url
     * @param request 请求参数（请求体）
     * @return
     */
    private static HTTPSamplerProxy getHttpSamplerProxy2(String url, String port, String api, String request) {
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        Arguments HTTPsamplerArguments = new Arguments();
        HTTPArgument httpArgument = new HTTPArgument();
        httpArgument.setProperty(new BooleanProperty("HTTPArgument.always_encode", false));
        httpArgument.setProperty(new StringProperty("Argument.value", request));
        httpArgument.setProperty(new StringProperty("Argument.metadata", "="));
        ArrayList<TestElementProperty> list1 = new ArrayList<>();
        list1.add(new TestElementProperty("", httpArgument));
        HTTPsamplerArguments.setProperty(new CollectionProperty("Arguments.arguments", list1));
        httpSamplerProxy.setProperty(new TestElementProperty("HTTPsampler.Arguments", HTTPsamplerArguments));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.domain", url));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.port", port));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.protocol", "http"));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.path", api));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.method", "POST"));
        // JMETER_ENCODING这个是我定义的常量，设置的编码是UTF-8，后面还有其他地方用到这个常量
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.contentEncoding", JMETER_ENCODING));
        httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.follow_redirects", true));
        httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.postBodyRaw", true));
        httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.auto_redirects", false));
        httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.use_keepalive", true));
        httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.DO_MULTIPART_POST", false));
        httpSamplerProxy.setProperty(new StringProperty("TestElement.gui_class", "org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui"));
        httpSamplerProxy.setProperty(new StringProperty("TestElement.test_class", "org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy"));
        httpSamplerProxy.setProperty(new StringProperty("TestElement.name", "HTTP Request"));
        httpSamplerProxy.setProperty(new StringProperty("TestElement.enabled", "true"));
        httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.postBodyRaw", true));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.embedded_url_re", ""));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.connect_timeout", ""));
        httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.response_timeout", ""));
        return httpSamplerProxy;
    }

    //创建信息头
    /**
     * 设置请求头信息
     * @return
     */
    private static HeaderManager getHeaderManager() {
        ArrayList<TestElementProperty> headerMangerList = new ArrayList<>();
        HeaderManager headerManager = new HeaderManager();
        Header header = new Header("Content-Type", "application/json");
        TestElementProperty HeaderElement = new TestElementProperty("", header);
        headerMangerList.add(HeaderElement);

        headerManager.setEnabled(true);
        headerManager.setName("HTTP Header Manager");
        headerManager.setProperty(new CollectionProperty(HeaderManager.HEADERS, headerMangerList));
        headerManager.setProperty(new StringProperty(TestElement.TEST_CLASS, HeaderManager.class.getName()));
        headerManager.setProperty(new StringProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName()));
        return headerManager;
    }

    //设置监听器中的结果（查看结果树）

}
