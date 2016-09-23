package com.shending.restful.interception;

import com.shending.restful.json.BaseJson;
import com.shending.support.Tools;
import com.shending.support.exception.AccountNotExistException;
import java.util.Date;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import org.jboss.weld.context.ejb.Ejb;

/**
 * 日志拦截器
 *
 * @author Yin.Weilong
 */
@Stateless
public class AccountInterceptor {

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
//        String beanClassName = ctx.getClass().getName();
//        String businessMethodName = ctx.getMethod().getName();
//        String target = beanClassName + "." + businessMethodName;
//        long startTime = System.currentTimeMillis();
//        System.out.println("Invoking " + target);
//        System.out.println("Parameters " + ctx.getParameters());
//        Object[] parameters = ctx.getParameters();
//        for (Object o : parameters) {
//            if (o instanceof HttpServletRequest) {
//                HttpServletRequest request = (HttpServletRequest) o;
//                //记录请求日志
//                String ip = Tools.getIpAddr(request);
//                aLaDingService.saveLogRequest(beanClassName, ip, businessMethodName);
//            }
//        }
        BaseJson bj = BaseJson.getBaseJson();
//        String methodName = ctx.getMethod().getName();
//        System.out.println("Method Name " + methodName);
//        System.out.println("Timer " + ctx.getTimer());
        try {
            return ctx.proceed();
        } catch (AccountNotExistException e) {
            bj.setSuccess("-1");
            bj.setMsg("请先登录！");
            return Tools.caseObjectToJson(bj);
        } catch (NumberFormatException e) {
            bj.setMsg("NumberFormatException");
            return Tools.caseObjectToJson(bj);
        } catch (Throwable t) {
            t.printStackTrace();
            bj.setMsg(t.getMessage());
            return Tools.caseObjectToJson(bj);
        } finally {
//            System.out.println("Exiting " + target);
//            long totalTime = System.currentTimeMillis() - startTime;
//            System.out.println("Business method " + businessMethodName + "in " + beanClassName + "takes " + totalTime + "ms to execute");
        }
    }
}
