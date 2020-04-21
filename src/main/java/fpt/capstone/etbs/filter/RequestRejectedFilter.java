package fpt.capstone.etbs.filter;

import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(0)
@Slf4j
public class RequestRejectedFilter  extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (RequestRejectedException e) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            log.warn("request_rejected: remote={}, user_agent={}, request_url={}",
                     req.getRemoteHost(),
                     req.getHeader(HttpHeaders.USER_AGENT),
                     req.getRequestURL(),
                     e);
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
