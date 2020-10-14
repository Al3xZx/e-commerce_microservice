package com.alessandro.apigateway.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class PreFilterLog extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(PreFilterLog.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info(request.getMethod() + " richiesta a " + request.getRequestURL().toString());
//        System.out.println(request.getMethod() + " richiesta a " + request.getRequestURL().toString());
        return null;
    }
}
