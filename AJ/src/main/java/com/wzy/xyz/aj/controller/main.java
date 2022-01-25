package com.wzy.xyz.aj.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wzy.xyz.aj.entity.HttpRequestInfo;
import com.wzy.xyz.aj.entity.ApiResult;

import java.io.*;

import com.wzy.xyz.aj.util.ApiResultHandler;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@RestController
public class main {

    @PostMapping("/generate")
    public void CreateJmx(HttpServletResponse res,@RequestBody String jsonData) {
        //jmeter的安装路径
        String jmeterPath = "/root/apache-jmeter-5.4-2.3";
        File jmeterHome = new File(jmeterPath);
        if (jmeterHome!=null){

        }
        // 分隔符
        String slash = System.getProperty("file.separator");
        BufferedInputStream bis = null;
        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {

                // 初始化压测引擎
                StandardJMeterEngine jmeter = new StandardJMeterEngine();
                // JMeter初始化(属性、日志级别、区域设置等)
                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLocale();

                // JMeter测试计划，基本上是JOrphan HashTree
                HashTree testPlanTree = new HashTree();

                // Loop Controller 循环控制
                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
                loopController.initialize();

                // Thread Group 线程组
                ThreadGroup threadGroup = new ThreadGroup();
                threadGroup.setName("线程组");
                threadGroup.setNumThreads(1);
                threadGroup.setRampUp(1);
                threadGroup.setSamplerController(loopController);
                threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
                threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

                // Test Plan 测试计划
                TestPlan testPlan = new TestPlan("测试计划");
                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

                //构建
                testPlanTree.add(testPlan);
                HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);

                JSONObject object = JSON.parseObject(jsonData);
                String str = object.getString("data");

                List<HttpRequestInfo> list = JSONObject.parseArray(str, HttpRequestInfo.class);
                for (HttpRequestInfo samp : list) {
                    //简化的http请求
                    HTTPSamplerProxy Sampler = new HTTPSamplerProxy();
                    Sampler.setName(samp.getName());
                    Sampler.setDomain(samp.getIpAddress());
                    Sampler.setPort(samp.getPortNumber());
                    Sampler.setPath(samp.getRoute());
                    Sampler.setMethod(samp.getRequestMethod());
                    Sampler.setContentEncoding("UTF-8");
                    Sampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                    Sampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

                    threadGroupHashTree.add(Sampler);
                }
                try {
                    SaveService.saveTree(testPlanTree, new FileOutputStream("example.jmx"));
                    try {
                        String path="example.jmx";
                        // 以流的形式下载文件。
                        File file = new File(path);
                        InputStream fis = new BufferedInputStream(new FileInputStream(path));
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        fis.close();
                        // 清空response
                        res.reset();
                        // 设置response的Header
                        res.addHeader("Content-Disposition", "attachment;filename=" + "example.jmx");
                        res.addHeader("Content-Length", "" + file.length());
                        OutputStream toClient = new BufferedOutputStream(res.getOutputStream());
                        res.setContentType("application/octet-stream");
                        toClient.write(buffer);
                        toClient.flush();
                        toClient.close();

                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                }catch (Exception e1){
                    System.out.println(e1);
                    e1.printStackTrace();
                }
            }
        }

    }

    @GetMapping("/test")
    public String hello(){
        return "hello,world";
    }

}