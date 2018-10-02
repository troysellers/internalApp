package filters;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import utils.SecurityUtils;

public class RateLimitter implements Filter {

	private Logger log;
	
	private Bucket createNewBucket() {
		long overdraft = 50;
		Refill refill = Refill.greedy(10, Duration.ofSeconds(1));
		Bandwidth limit = Bandwidth.classic(overdraft, refill);
		return Bucket4j.builder().addLimit(limit).build();
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpSession session = request.getSession(true);
		
		//TODO - this third party app key needs to be implemented properly
		String appKey = SecurityUtils.getThirdPartyAppKey();
		Bucket bucket = (Bucket)session.getAttribute("throttler-"+appKey);
		
		log.info("Trying to limit the API rates");
		
		if (bucket == null) {
			bucket = createNewBucket();
			session.setAttribute("throttler-"+appKey, bucket);
		} 
		
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		
		if (probe.isConsumed()) {
			httpResponse.setHeader("X-Rate-Limit-Remaining", "" + probe.getRemainingTokens());
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
			httpResponse.setContentType("text/plain");
			httpResponse.setStatus(429);
			httpResponse.getWriter().append("Too many requests");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		log = LoggerFactory.getLogger(this.getClass());
		log.info("Initialised {}", this.getClass());
		
	}

}
