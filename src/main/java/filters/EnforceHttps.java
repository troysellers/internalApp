package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnforceHttps implements Filter {

	public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
	private Logger log;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		
		log.info("trying to enforce https");
		
		if(request.getHeader(X_FORWARDED_PROTO) != null ) {
			if(request.getHeader(X_FORWARDED_PROTO).indexOf("https") != 0) {
				String pathInfo = (request.getPathInfo() != null ) ? request.getPathInfo() : "";
				response.sendRedirect("https://"+request.getServerName()+pathInfo);
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);	
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		log = LoggerFactory.getLogger(this.getClass());
		log.info("Initialised {}", this.getClass());
	}
}
