package fpt.capstone.etbs.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class GZipFilter implements Filter {


  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) {
    HttpServletRequest request = (HttpServletRequest) req;
    String encoding = request.getHeader("Content-Encoding");
    if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
      req = new GZIPServletRequestWrapper(request);
    }

    try {
      fc.doFilter(req, res);
    } catch (IOException | ServletException e) {
      log.error("Can't parse GZip request", e);
    }
  }

  private static class GZIPServletRequestWrapper extends HttpServletRequestWrapper {

    public GZIPServletRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      return new GZIPServletInputStream(super.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
      return new BufferedReader(
          new InputStreamReader(new GZIPServletInputStream(super.getInputStream())));
    }
  }

  private static class GZIPServletInputStream extends ServletInputStream {

    private InputStream input;

    public GZIPServletInputStream(InputStream input) throws IOException {
      this.input = new GZIPInputStream(input);
    }

    @Override
    public int read() throws IOException {
      return input.read();
    }

    @Override
    public boolean isFinished() {
      return false;
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }
  }
}
